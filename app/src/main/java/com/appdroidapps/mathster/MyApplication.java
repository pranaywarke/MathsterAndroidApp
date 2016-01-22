package com.appdroidapps.mathster;

import android.app.Application;

import com.clevertap.android.sdk.ActivityLifecycleCallback;

/**
 * Created by pranay on 27/11/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
