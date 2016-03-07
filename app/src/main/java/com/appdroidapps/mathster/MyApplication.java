package com.appdroidapps.mathster;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;

import com.appdroidapps.mathster.activities.RootActivity;
import com.clevertap.android.sdk.ActivityLifecycleCallback;

/**
 * Created by pranay on 27/11/15.
 */
public class MyApplication extends Application {

    public static Typeface mathsterFont = null;

    private static MyApplication that ;
    @Override
    public void onCreate() {
        that = this;
//        CleverTapAPI.setDebugLevel(1277182231);
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        //   Branch.getAutoInstance(this);
        mathsterFont = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "fonts/mathster_font.ttf");
    }


    public static Paint createCheckerBoard(int pixelSize) {
        Bitmap bitmap = Bitmap.createBitmap(pixelSize * 2, pixelSize * 2, Bitmap.Config.ARGB_8888);

        Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(ContextCompat.getColor(that, R.color.mathster3));

        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, pixelSize, pixelSize);
        canvas.drawRect(rect, fill);
        rect.offset(pixelSize, pixelSize);
        canvas.drawRect(rect, fill);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(bitmap, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT));
        return paint;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
