package com.appdroidapps.mathster.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroidapps.mathster.BarChart;
import com.appdroidapps.mathster.BarData;
import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.utils.DynamicButton;
import com.appdroidapps.mathster.utils.DynamicTextView;
import com.appdroidapps.mathster.utils.MessagingUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranay on 02/11/15.
 */
public class MainActivity extends RootActivity {

    private FrameLayout frame1, frame2;
    private AlertDialog builder;
    private DynamicTextView highScore, highScoreLabel;
    private DynamicButton go_button;
    protected InterstitialAd mInterstitialAd;
    protected View buttonsGroup;
    //  private BarChart histogram;

    private DynamicButton topFive, showDailyChart, showLeaderBoardButton;
    private DynamicTextView rate, share;

    protected void requestNewInterstitial() {
        if (mInterstitialAd != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("3BF3563231EC0E15536D1F94441DBD55")
                    .build();

            mInterstitialAd.loadAd(adRequest);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_old);
        buttonsGroup = (View) findViewById(R.id.buttonGroup);
        //mnm = (TextView) findViewById(R.id.mnm);
        frame1 = (FrameLayout) findViewById(R.id.frame1);
        frame2 = (FrameLayout) findViewById(R.id.frame2);
        String welcomeMessage = MessagingUtils.getWelcomeMessage();
        if (welcomeMessage != null) {
            showToast(welcomeMessage);
        }
        highScore = (DynamicTextView) findViewById(R.id.topScore);
        highScore.setTextColor(mathster_orange);
        highScoreLabel = (DynamicTextView) findViewById(R.id.highScoreLabel);
        highScoreLabel.setTextColor((mathster_orange));
        go_button = (DynamicButton) findViewById(R.id.go_button);
        showLeaderBoardButton = (DynamicButton) findViewById(R.id.showLeaderBoard);
        topFive = (DynamicButton) findViewById(R.id.frequencyChart);
        showDailyChart = (DynamicButton) findViewById(R.id.dailyChart);
        rate = (DynamicTextView) findViewById(R.id.rateMathster);
        rate.setTextColor(mathster_orange);
        share = (DynamicTextView) findViewById(R.id.share);
        share.setTextColor(mathster_orange);
        showDailyChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleverTapAPI.event.push("Daily HighScore Clicked");
                showHistoGram(HistoGramType.DailyTopScore);
            }
        });
        topFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleverTapAPI.event.push("Top5 Table Clicked");
                showHistoGram(HistoGramType.TopFiveScores);

            }
        });
        if (SHOW_ADS) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-2882064284132215/5034737489");
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    requestNewInterstitial();
                }
            });
            requestNewInterstitial();
        }

        showLeaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cleverTapAPI.event.push("LeaderBoard Clicked");
                Intent in = new Intent(MainActivity.this, ScoreActivity.class);
                in.setAction(ScoreActivity.ACTION_VIEW_LEADERBOARD);
                startActivity(in);

            }
        });

        rate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                btnRateAppOnClick();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("Screen", "main");
                cleverTapAPI.event.push("Share Clicked", map);
                btnShareOnClick();
            }
        });
        go_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                cleverTapAPI.event.push("Go Started");
                Intent intent = new Intent(MainActivity.this, MnMActivity.class);
                startActivity(intent);
            }
        });

    }


    protected void showBar(BarChart b, HistoGramType type) {
        ArrayList<BarData> data_list = new ArrayList<BarData>();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch (type) {
            case TopFiveScores:
                JSONArray top = RootActivity.getLastBestScore();
                try {
                    if (top.length() > 0)
                        data_list.add(new BarData("1st", top.getInt(0)));
                    if (top.length() > 1)
                        data_list.add(new BarData("2nd", top.getInt(1)));
                    if (top.length() > 2)
                        data_list.add(new BarData("3rd", top.getInt(2)));
                    if (top.length() > 3)
                        data_list.add(new BarData("4th", top.getInt(3)));
                    if (top.length() > 4)
                        data_list.add(new BarData("5th", top.getInt(4)));
                    b.setBarData(data_list, metrics.heightPixels * 0.7);
                } catch (Exception e) {

                }
                break;
            case ScoreFrequency:
                int[] histo = RootActivity.getHistogramValues();
                data_list.add(new BarData("<100", histo[0] + histo[1]));
                data_list.add(new BarData("101\nto\n200", histo[2]));
                data_list.add(new BarData("201\nto\n300", histo[3]));
                data_list.add(new BarData("301\nto\n400", histo[4]));
                data_list.add(new BarData("401\nto\n500", histo[5]));
                data_list.add(new BarData("501\nto\n600", histo[6]));
                data_list.add(new BarData("600<", histo[7]));
                b.setBarData(data_list, metrics.heightPixels * 0.7);
                break;
            case DailyTopScore:

                JSONArray arr = RootActivity.getDailyHistogramValues();
                //    histo = new int[]{23,45,22,33,22,32,21,25};

                if (arr != null) {

                    Calendar cal = Calendar.getInstance();

                    for (int i = arr.length() - 1; i >= 0; i--) {
                        try {
                            String key = i + "\n" + (i == 1 ? "day" : "days") + "\nago";
                            if (i == 0) {
                                key = "Today";
                            } else if (i == arr.length() - 1) {
                                key = "Until " + i + "\ndays\nago";
                            }
                            data_list.add(new BarData(key, arr.getInt(i)));

                        } catch (Exception e) {
                            data_list.add(new BarData(i + "", 0));
                        }
                    }

                    b.setBarData(data_list, metrics.heightPixels * 0.7);
                }
                break;
        }

    }


    //On click event for rate this app button
    public void btnRateAppOnClick() {

        cleverTapAPI.event.push("Rate Clicked");
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.appdroidapps.mathster"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
        }
    }


    protected void onResume() {
        super.onResume();
        int ts = RootActivity.getMnMTopScore();
        //  histogram.setVisibility(View.GONE);

        if (ts != -1) {
            highScore.setLines(2);
            highScore.setGravity(Gravity.CENTER);
            highScore.setText(ts + "");
            frame1.setVisibility(View.VISIBLE);
            frame2.setVisibility(View.VISIBLE);
        } else {
            frame1.setVisibility(View.INVISIBLE);
            frame2.setVisibility(View.INVISIBLE);
        }
    }

    enum HistoGramType {
        ScoreFrequency, DailyTopScore, TopFiveScores
    }

    public void showHistoGram(HistoGramType type) {


        ScrollView scroll = new ScrollView(this);
        View dialoglayout = getLayoutInflater().inflate(R.layout.chart_dialog_layout, null);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 10.f);
        dialoglayout.setLayoutParams(layoutParams);

        TextView titletextView = (TextView) dialoglayout.findViewById(R.id.titleChart);
        BarChart barChart = (BarChart) dialoglayout.findViewById(R.id.contentChart);
        TextView helpText = (TextView) dialoglayout.findViewById(R.id.helpText);
        Button close = (Button) dialoglayout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder != null && builder.isShowing()) {
                    builder.cancel();
                }
            }
        });
        switch (type) {
            case TopFiveScores:
                titletextView.setText("Top 5 Scores");
                helpText.setText("Your top 5 scores");
                break;
            case DailyTopScore:
                titletextView.setText("Daily High Scores");
                helpText.setText("Helps you track if you are getting better everyday");
                break;
            case ScoreFrequency:
                titletextView.setText("Frequency of your scores");
                helpText.setText("How often you score between the above ranges");
                break;
        }

        scroll.addView(dialoglayout);
        AlertDialog.Builder bd = new AlertDialog.Builder(this).setView(scroll);

        builder = bd.create();
        builder.show();
        builder.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        showBar(barChart, type);

    }
}
