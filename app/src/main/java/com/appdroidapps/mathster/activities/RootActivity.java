package com.appdroidapps.mathster.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.appdroidapps.mathster.R;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by pranay on 14/11/15.
 */
public class RootActivity extends AppCompatActivity {


    private static final String SUFFIX = "7";

    /*  protected int[] mathsterColors = new int[]{
              Color.parseColor("#801638"),
              Color.parseColor("#c22326"),
              Color.parseColor("#f37338"),
              Color.parseColor("#027878")};*/
    static class MathsterColor {
        public int color;
        public int res;

        MathsterColor(int color, int res) {
            this.color = color;
            this.res = res;
        }
    }

    public static List<MathsterColor> mathsterColors = new ArrayList<>();

    static {
        mathsterColors.add(new MathsterColor(Color.parseColor("#c22326"), R.drawable.simple_button_bg_mathster_red));
        mathsterColors.add(new MathsterColor(Color.parseColor("#801638"), R.drawable.simple_button_bg_mathster_purple));
        mathsterColors.add(new MathsterColor(Color.parseColor("#f37338"), R.drawable.simple_button_bg_mathster_orange));
        mathsterColors.add(new MathsterColor(Color.parseColor("#027878"), R.drawable.simple_button_bg_mathster_green));
        Collections.shuffle(mathsterColors);
    }

    public static int mathster_red = Color.parseColor("#c22326");
    public static int mathster_purple = Color.parseColor("#801638");
    public static int mathster_orange = Color.parseColor("#f37338");
    public static int mathster_green = Color.parseColor("#027878");


    protected static final boolean SHOW_ADS = false;
    private static final String MNM_KEY = "score" + SUFFIX;
    private static final String LAST_UPDATED_LEADERBOARD_SCORE = "last_up_score" + SUFFIX;
    private static final String LEADERBOARD_SYNC = "l_sync_n" + SUFFIX;
    protected static SharedPreferences sharedPreferences;
    protected static SharedPreferences.Editor editor;
    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static final String HISTOGRAM_FREQ_PREFIX_STR = "Histogram_" + SUFFIX;
    private static final String HISTOGRAM_DAILY_PREFIX_STR = "Histogram_Daily_" + SUFFIX;
    private static final String HISTOGRAM_LAST_TOP_PREFIX_STR = "Histogram_Last_" + SUFFIX;
    private static final String CUSTOM_KEYS = "Custom_Keys" + SUFFIX;
    private static final int LAST_DAYS_SUPPORTED = 5;
    private static final int LAST_SCORES_SUPPORTED = 5;
    public static CleverTapAPI cleverTapAPI;

    enum HistogramBuckets {

        bucket1("< 50", 0, 50),
        bucket2("51 - 100", 50, 100),
        bucket3("101 - 200", 101, 200),
        bucket4("201 - 300", 201, 300),
        bucket5("301 - 400", 301, 400),
        bucket6("401 - 500", 401, 500),
        bucket7("501 - 600", 501, 600),
        bucket8("600 >", 600, Integer.MAX_VALUE);

        HistogramBuckets(String label, int min, int max) {
            this.label = label;
            this.max = max;
            this.min = min;
        }

        String label;
        int min, max;
        private static HistogramBuckets[] vals = values();

        public static HistogramBuckets getBucket(long score) {
            if (score == 0) return null; // not storing 0 scores
            if (score < 50) return bucket1;
            if (score > 600) return bucket8;
            for (int i = 0; i < vals.length - 1; i++) {

                if (score >= vals[i].min && score < vals[i].max) {
                    return vals[i];
                }
            }
            return null;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        synchronized (this) {
            if (sharedPreferences == null) {
                sharedPreferences = getSharedPreferences("com.appdroidapps.mathster", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
            }
            if (cleverTapAPI == null) {
                try {
                    cleverTapAPI = CleverTapAPI.getInstance(this);
                    cleverTapAPI.enablePersonalization();
                } catch (CleverTapMetaDataNotFoundException e) {
                    e.printStackTrace();
                } catch (CleverTapPermissionsNotSatisfied cleverTapPermissionsNotSatisfied) {
                    cleverTapPermissionsNotSatisfied.printStackTrace();
                }
            }

        }


    }

    public static String getFromSharedPrefrences(String key) {
        return sharedPreferences.getString(CUSTOM_KEYS + "_" + key, null);
    }

    public static void setToSharedPreferences(String key, String val) {
        editor.putString(CUSTOM_KEYS + "_" + key, val);
        editor.commit();
    }

    public static int getMnMTopScore() {
        return (int) sharedPreferences.getLong(MNM_KEY, -1);
    }

    public static void setMnMTopScore(long score) {
        editor.putLong(MNM_KEY, (long) score);
        editor.commit();
    }

    public static void setHistogramValues(long score) {

        HistogramBuckets b = HistogramBuckets.getBucket(score);
        if (b != null) {
            int prev = sharedPreferences.getInt(HISTOGRAM_FREQ_PREFIX_STR + b.ordinal(), 0);
            prev++;
            editor.putInt(HISTOGRAM_FREQ_PREFIX_STR + b.ordinal(), prev);
        }
        fixDailyValues(score);
        setLastBestScore(score);
        editor.commit();
    }

    private static boolean contains(JSONArray haystack, long needle) {
        try {
            for (int i = 0; i < haystack.length(); i++) {
                if (haystack.getLong(i) == needle) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static JSONArray getLastBestScore() {
        JSONArray arr = null;
        String str = sharedPreferences.getString(HISTOGRAM_LAST_TOP_PREFIX_STR, null);
        try {
            if (str != null) {
                arr = new JSONArray(str);

            }
        } catch (Exception e) {

        }
        if (arr == null) {
            arr = new JSONArray();
            for (int i = 0; i < LAST_SCORES_SUPPORTED; i++) {
                arr.put(0l);
            }
        }
        return arr;
    }

    private static void setLastBestScore(long score) {
        JSONArray arr = null;
        String str = sharedPreferences.getString(HISTOGRAM_LAST_TOP_PREFIX_STR, null);
        try {
            if (str != null) {
                arr = new JSONArray(str);

            }
        } catch (Exception e) {

        }
        if (arr == null) {
            arr = new JSONArray();
            for (int i = 0; i < LAST_SCORES_SUPPORTED; i++) {
                arr.put(0l);
            }
        }

        try {

            if (contains(arr, score)) {
                return;
            }
            arr.put(score);
            for (int c = 0; c < arr.length() - 1; c++) {
                for (int d = 0; d < arr.length() - c - 1; d++) {
                    if (arr.getLong(d) < arr.getLong(d + 1)) /* For descending order use < */ {
                        long swap = arr.getLong(d);
                        arr.put(d, arr.get(d + 1));
                        arr.put(d + 1, swap);
                    }
                }
            }

            JSONArray _arr = new JSONArray();
            for (int i = 0; i < LAST_SCORES_SUPPORTED; i++) {
                _arr.put(arr.get(i));
            }

            editor.putString(HISTOGRAM_LAST_TOP_PREFIX_STR, _arr.toString());
        } catch (Exception e) {

        }


    }

    public static int[] getHistogramValues() {
        int[] r = new int[HistogramBuckets.vals.length];
        for (HistogramBuckets b : HistogramBuckets.vals) {
            int v = sharedPreferences.getInt(HISTOGRAM_FREQ_PREFIX_STR + b.ordinal(), 0);
            r[b.ordinal()] = v;
        }
        return r;
    }

    public static JSONArray getDailyHistogramValues() {
        return fixDailyValues(-1);
    }

    public static int getLeaderBoardLastUpdatedTopScore() {
        return (int) sharedPreferences.getLong(LAST_UPDATED_LEADERBOARD_SCORE, -1);
    }

    public static void setLeaderBoardLastUpdatedTopScore(long score) {

        editor.putLong(LAST_UPDATED_LEADERBOARD_SCORE, score);
        editor.commit();
    }

    public static boolean isLeaderBoardSynced() {

        long sync = sharedPreferences.getLong(LEADERBOARD_SYNC, -1);

        return sync != -1 && sync < System.currentTimeMillis() - 1000 * 60 * 60 * 48;
    }

    public static void setLeaderBoardSynced(long sync) {

        editor.putLong(LEADERBOARD_SYNC, sync);
        editor.commit();
    }

    protected void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        View view = toast.getView();

        GradientDrawable border = new GradientDrawable();
        border.setStroke(2, mathster_orange); //black border with full opacity
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(border);
        } else {
            view.setBackground(border);
        }
        view.invalidate();
        toast.show();
    }

    public static Long getScoreOfTheDay() {
        Long r = null;
        String str = sharedPreferences.getString(HISTOGRAM_DAILY_PREFIX_STR, null);
        try {
            if (str != null) {
                JSONArray arr = new JSONArray(str);
                return arr.getLong(0);
            }
        } catch (Exception e) {

        }
        return r;
    }

    protected static JSONArray fixDailyValues(long score) {
        JSONArray arr = null;
        try {

            String str = sharedPreferences.getString(HISTOGRAM_DAILY_PREFIX_STR, null);

            if (str == null) {
                arr = new JSONArray();
                for (int i = 0; i < LAST_DAYS_SUPPORTED; i++) {
                    arr.put(0l);
                }
            } else {
                arr = new JSONArray(str);
            }
            int ts = sharedPreferences.getInt(HISTOGRAM_DAILY_PREFIX_STR + "_ts", -1);
            int today = Integer.parseInt(sdf.format(new Date()));
            //    today = 20161010;
            if (ts == -1) {
                ts = today;
            }
            int diff = today - ts;
            for (int i = 0; i < diff; i++) {
                for (int j = arr.length() - 1; j > 0; j--) {
                    long numToSwap = arr.getLong(j - 1);
                    if (j == arr.length() - 1) {
                        if (numToSwap < arr.getLong(j)) {
                            numToSwap = arr.getLong(j);
                        }
                    }
                    arr.put(j, numToSwap);
                    arr.put(j - 1, 0l);
                }
            }
            if (score > arr.getLong(0) && score > -1) {
                arr.put(0, score);
            }
            editor.putString(HISTOGRAM_DAILY_PREFIX_STR, arr.toString());
            editor.putInt(HISTOGRAM_DAILY_PREFIX_STR + "_ts", today);

        } catch (Exception e) {

        }
        return arr;
    }

    //On click event for rate this app button
    public void btnShareOnClick() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        int highscore = RootActivity.getMnMTopScore();
        String text = "Hey check out Mathster! Get it here https://play.google.com/store/apps/details?id=com.appdroidapps.mathster";
        if (highscore > 0) {
            text = "Can you beat my score of " + highscore + " on Mathster. Get it here https://play.google.com/store/apps/details?id=com.appdroidapps.mathster";
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        try {
            startActivity(Intent.createChooser(sendIntent, "Share on"));
        } catch (Exception e) {
            Toast.makeText(this, "Error trying to share, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private static int diffInDays(int l, int r) {
        int y = l / 10000, m = (l % 10000) / 100, d = l % 100;
        int ry = r / 10000, rm = (r % 10000) / 100, rd = r % 100;
        int days = dayOfYear(y, m, d) - dayOfYear(ry, rm, rd);

        if (ry != y) {
            days += isLeapYear(ry) ? 366 : 365;
        }

        return days;
    }

    public static int dayOfYear(int y, int m, int d) {
        if (isLeapYear(y)) {
            return cmml[m - 1] + d;
        }
        return cmm[m - 1] + d;
    }

    protected static int[] mm = new int[]{
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };
    protected static int[] mml = new int[]{
            31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    protected static int[] cmm = new int[]{
            0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};

    protected static int[] cmml = new int[]{
            0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    public static boolean isLeapYear(int y) {
        return (((y % 4 == 0) && (y % 100 != 0)) || (y % 400 == 0));
    }

}
