package com.appdroidapps.mathster.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.appdroidapps.mathster.R;


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
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(that, MainActivity.class);
                startActivity(i);
                finish();

            }
        });
        t.start();
    }

}
