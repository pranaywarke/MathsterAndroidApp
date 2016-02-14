package com.appdroidapps.mathster;

import android.app.Application;

import com.appdroidapps.mathster.activities.RootActivity;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;

import io.branch.referral.Branch;

/**
 * Created by pranay on 27/11/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
//        CleverTapAPI.setDebugLevel(1277182231);
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        Branch.getAutoInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
