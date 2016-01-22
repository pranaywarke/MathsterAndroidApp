package com.appdroidapps.mathster.activities;

import android.app.Service;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.query.Question;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by pranay on 14/11/15.
 */
public abstract class PlayFieldActivity extends RootActivity {


    final protected Handler handler = new Handler();
    protected Vibrator vibrator = null;

    private TextView question;
    protected View parent_parent;
    private Button option1, option2, option3, option4;

    protected int rightAnswerCount = 0, wrongAnswerCount = 0;

    protected TextView highScore;

    public ProgressBar progressBar;
    public TextSwitcher currentScore;
    public TextSwitcher bonusScore;
    AdView mAdView;

    public void loadBanner() {
        if (mAdView != null && SHOW_ADS) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("3BF3563231EC0E15536D1F94441DBD55")
                    .build();

            mAdView.loadAd(adRequest);
        }
    }

    protected int currentHighScore = -1;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_field);
        parent_parent = findViewById(R.id.parent_parent);
        //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mAdView = (AdView) findViewById(R.id.adView);
        if (SHOW_ADS) {

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
            loadBanner();
        } else {
            mAdView.setVisibility(View.GONE);
        }

    }

    int noOfrightAnswer = 0;
    View.OnClickListener onclick = new View.OnClickListener() {
        public void onClick(View v) {
            int clikedAnswer = Integer.parseInt(((TextView) v).getText() + "");
            if (clikedAnswer == _q.getOptions()[_q.getIdx()]) {
                if (noOfrightAnswer++ % 5 == 0) {
                    loadBanner();
                }
                rightAnswerCount = rightAnswerCount + 10;
                _q.setAnswered(true);


                onRightAnswer(clikedAnswer);

            } else {
                wrongAnswerCount = wrongAnswerCount - 10;
                onWrongAnswer(clikedAnswer);
            }
        }
    };

    public abstract void onRightAnswer(int answer);

    public abstract void onWrongAnswer(int answer);

    protected Question _q = null;

    protected void showQuestion(Question _q) {
        this._q = _q;
        question.setText(_q.getQueryString());
        Integer[] option = _q.getOptions();
        option1.setText(option[0] + "");
        option2.setText(option[1] + "");
        option3.setText(option[2] + "");
        option4.setText(option[3] + "");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        question = (TextView) findViewById(R.id.question);
        option1 = (Button) findViewById(R.id.option1);
        option2 = (Button) findViewById(R.id.option2);
        option3 = (Button) findViewById(R.id.option3);
        option4 = (Button) findViewById(R.id.option4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        currentScore = (TextSwitcher) findViewById(R.id.textSwitcher);
        bonusScore = (TextSwitcher) findViewById(R.id.bonustextSwitcher);
        bonusScore.removeAllViews();
        bonusScore.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                // create new textView and set the properties like clolr, size etc
                TextView bonus_textView = new TextView(PlayFieldActivity.this);
                bonus_textView.setGravity(Gravity.CENTER);
                //   bonus_textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,22);
                bonus_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                bonus_textView.setTypeface(Typeface.DEFAULT_BOLD);
                bonus_textView.setTextColor(Color.GREEN);
                bonus_textView.setShadowLayer(1.5f, -1, 1, Color.BLACK);
                return bonus_textView;
            }
        });

        currentScore.removeAllViews();
        currentScore.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                // create new textView and set the properties like clolr, size etc
                TextView currentscore_textView = new TextView(PlayFieldActivity.this);
                currentscore_textView.setGravity(Gravity.CENTER);
                //   currentscore_textView.setTextSize(24);
                currentscore_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                currentscore_textView.setTextColor(Color.BLACK);
                return currentscore_textView;
            }
        });

        // Declare the in and out animations and initialize them
        Animation in_bonous = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out_bonous = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);


        bonusScore.setInAnimation(in_bonous);
        bonusScore.setOutAnimation(out_bonous);
        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        // set the animation type of currentScore
        currentScore.setInAnimation(in);
        highScore = (TextView) findViewById(R.id.highscore);
        currentHighScore = getMnMTopScore();
        if (currentHighScore > 0) {
            highScore.setText(currentHighScore + " more to go");
        } else {
            highScore.setText("");
        }
        option1.setOnClickListener(onclick);

        option2.setOnClickListener(onclick);

        option3.setOnClickListener(onclick);
        option4.setOnClickListener(onclick);
        vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        rightAnswerCount = 0;
        wrongAnswerCount = 0;

    }


    public enum GAME_OVER_REASON {
        WRONG_ANSWER, TIMEOUT;
    }
}
