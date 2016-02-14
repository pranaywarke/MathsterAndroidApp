package com.appdroidapps.mathster.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.appdroidapps.mathster.R;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;


/**
 * Created by pranay on 14/11/15.
 */
public class SplashActivity extends RootActivity {


    Activity that = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(that, LandingActivity.class);
                startActivity(i);
                finish();

            }
        });
        t.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Branch branch = Branch.getInstance();
            branch.initSession(new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error == null) {
                        // params are the deep linked params associated with the link that the user clicked before showing up
                        Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
                    }
                }
            }, this.getIntent().getData(), this);
        } catch (Exception e) {

        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}
