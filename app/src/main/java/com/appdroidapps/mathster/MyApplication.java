package com.appdroidapps.mathster;

import android.app.Application;

import com.clevertap.android.sdk.ActivityLifecycleCallback;

/**
 * Created by pranay on 27/11/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
//        CleverTapAPI.setDebugLevel(1277182231);
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        //   Branch.getAutoInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
