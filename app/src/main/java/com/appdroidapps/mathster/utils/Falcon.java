package com.appdroidapps.mathster.utils;

/**
 * Created by pranay on 07/02/16.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;

/**
 * Utility class to take screenshots of activity screen
 */
public final class Falcon {
    //region Constants

    private static final String TAG = "Falcon";

    //endregion

    //region Public API

    /**
     * Takes screenshot of provided activity and saves it to provided file.
     * File content will be overwritten if there is already some content.
     *
     * @param activity Activity of which the screenshot will be taken.
     * @param toFile   File where the screenshot will be saved.
     *                 If there is some content it will be overwritten
     * @throws UnableToTakeScreenshotException When there is unexpected error during taking screenshot
     */
    public static void takeScreenshot(Activity activity, final File toFile) {
        if (activity == null) {
            throw new IllegalArgumentException("Parameter activity cannot be null.");
        }

        if (toFile == null) {
            throw new IllegalArgumentException("Parameter toFile cannot be null.");
        }

        Bitmap bitmap = null;
        try {
            bitmap = takeBitmapUnchecked(activity);
            writeBitmap(bitmap, toFile);
        } catch (Exception e) {
            String message = "Unable to take screenshot to file " + toFile.getAbsolutePath()
                    + " of activity " + activity.getClass().getName();

            Log.e(TAG, message, e);
            throw UnableToTakeScreenshotException.wrap(message, e);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }

        Log.d(TAG, "Screenshot captured to " + toFile.getAbsolutePath());
    }

    /**
     * Takes screenshot of provided activity and puts it into bitmap.
     *
     * @param activity Activity of which the screenshot will be taken.
     * @return Bitmap of what is displayed in activity.
     * @throws UnableToTakeScreenshotException When there is unexpected error during taking screenshot
     */
    public static Bitmap takeScreenshotBitmap(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Parameter activity cannot be null.");
        }

        try {
            return takeBitmapUnchecked(activity);
        } catch (Exception e) {
            String message = "Unable to take screenshot to bitmap of activity "
                    + activity.getClass().getName();

            Log.e(TAG, message, e);
            throw UnableToTakeScreenshotException.wrap(message, e);
        }
    }


    //endregion

    //region Methods

    private static Bitmap takeBitmapUnchecked(Activity activity) throws InterruptedException {
        final List<ViewRootData> viewRoots = getRootViews(activity);
        View main = activity.getWindow().getDecorView();

        final Bitmap bitmap = Bitmap.createBitmap(main.getWidth(), main.getHeight(), ARGB_8888);

        // We need to do it in main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            drawRootsToBitmap(viewRoots, bitmap);
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        drawRootsToBitmap(viewRoots, bitmap);
                    } finally {
                        latch.countDown();
                    }
                }
            });

            latch.await();
        }

        return bitmap;
    }

    private static void drawRootsToBitmap(List<ViewRootData> viewRoots, Bitmap bitmap) {
        for (ViewRootData rootData : viewRoots) {
            drawRootToBitmap(rootData, bitmap);
        }
    }

    private static void drawRootToBitmap(ViewRootData config, Bitmap bitmap) {
        // now only dim supported
        if ((config._layoutParams.flags & FLAG_DIM_BEHIND) == FLAG_DIM_BEHIND) {
            Canvas dimCanvas = new Canvas(bitmap);

            int alpha = (int) (255 * config._layoutParams.dimAmount);
            dimCanvas.drawARGB(alpha, 0, 0, 0);
        }

        Canvas canvas = new Canvas(bitmap);
        canvas.translate(config._winFrame.left, config._winFrame.top);
        config._view.draw(canvas);
    }

    private static void writeBitmap(Bitmap bitmap, File toFile) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(toFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @SuppressWarnings("unchecked") // no way to check
    private static List<ViewRootData> getRootViews(Activity activity) {
        List<ViewRootData> rootViews = new ArrayList<>();

        Object globalWindowManager;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            globalWindowManager = getFieldValue("mWindowManager", activity.getWindowManager());
        } else {
            globalWindowManager = getFieldValue("mGlobal", activity.getWindowManager());
        }
        Object rootObjects = getFieldValue("mRoots", globalWindowManager);
        Object paramsObject = getFieldValue("mParams", globalWindowManager);

        Object[] roots;
        LayoutParams[] params;

        //  There was a change to ArrayList implementation in 4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            roots = ((List) rootObjects).toArray();

            List<LayoutParams> paramsList = (List<LayoutParams>) paramsObject;
            params = paramsList.toArray(new LayoutParams[paramsList.size()]);
        } else {
            roots = (Object[]) rootObjects;
            params = (LayoutParams[]) paramsObject;
        }

        for (int i = 0; i < roots.length; i++) {
            Object root = roots[i];

            Object attachInfo = getFieldValue("mAttachInfo", root);
            int top = (int) getFieldValue("mWindowTop", attachInfo);
            int left = (int) getFieldValue("mWindowLeft", attachInfo);

            Rect winFrame = (Rect) getFieldValue("mWinFrame", root);
            Rect area = new Rect(left, top, left + winFrame.width(), top + winFrame.height());

            View view = (View) getFieldValue("mView", root);
            rootViews.add(new ViewRootData(view, area, params[i]));
        }

        return rootViews;
    }

    private static Object getFieldValue(String fieldName, Object target) {
        try {
            return getFieldValueUnchecked(fieldName, target);
        } catch (Exception e) {
            throw UnableToTakeScreenshotException.wrap(e.getMessage(), e);
        }
    }

    private static Object getFieldValueUnchecked(String fieldName, Object target)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(fieldName, target.getClass());

        field.setAccessible(true);
        return field.get(target);
    }

    private static Field findField(String name, Class clazz) throws NoSuchFieldException {
        Class currentClass = clazz;
        while (currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new NoSuchFieldException("Field " + name + " not found for class " + clazz);
    }

    //endregion

    //region Constructors

    // No instances
    private Falcon() {
    }

    //endregion

    //region Nested classes

    /**
     * Custom exception thrown if there is some exception thrown during
     * screenshot capturing to enable better client code exception handling.
     */
    public static class UnableToTakeScreenshotException extends RuntimeException {
        private UnableToTakeScreenshotException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        /**
         * Factory method to avoid multiple wrapping. If there is already our exception,
         * just wrap the cause again
         */
        private static UnableToTakeScreenshotException wrap(String message, Exception ex) {
            if (ex instanceof UnableToTakeScreenshotException) {
                return new UnableToTakeScreenshotException(message, ex.getCause());
            }

            return new UnableToTakeScreenshotException(message, ex);
        }
    }

    private static class ViewRootData {
        private final View _view;
        private final Rect _winFrame;
        private final LayoutParams _layoutParams;

        public ViewRootData(View view, Rect winFrame, LayoutParams layoutParams) {
            _view = view;
            _winFrame = winFrame;
            _layoutParams = layoutParams;
        }
    }

    //endregion
}
