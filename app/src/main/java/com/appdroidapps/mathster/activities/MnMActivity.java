package com.appdroidapps.mathster.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.query.QueryInterface;
import com.appdroidapps.mathster.utils.MessagingUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranay on 14/11/15.
 */

public class MnMActivity extends PlayFieldActivity {


    private static final String images_folder_name = "shared_images";
    private static final String image_name = "image.png";
    private static Runnable r = null;

    private int progress;
    private AlertDialog builder;
    private boolean stop = false;
    private MnMActivity that = this;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        stop = false;
    }

    public void onRightAnswer(int answer) {
        if (stop) {
            return;
        }
        if (progress > 66) {
            bonusScore.setText("+3 XP");
            rightAnswerCount = rightAnswerCount + 3;
        } else if (progress > 33) {
            bonusScore.setText("+2 XP");
            rightAnswerCount = rightAnswerCount + 2;
        } else {
            bonusScore.setText("");
        }
        vibrator.vibrate(25);
        if (currentHighScore > 0) {
            int left = currentHighScore - rightAnswerCount;
            if (left >= 0) {
                highScore.setText(left + " more to go");
            } else {
                highScore.setText("It's a new high score");
            }

        }

        refresh();
        next();

    }

    public void onWrongAnswer(int answer) {
        refresh();
        vibrator.vibrate(500);
        showScore(GAME_OVER_REASON.WRONG_ANSWER);
    }

    public void onTimeOut() {

        stop = true;
        refresh();
        vibrator.vibrate(500);
        showScore(GAME_OVER_REASON.TIMEOUT);
    }


    private void next() {
        currentScore.setText("" + rightAnswerCount);
        showQuestion(QueryInterface.getQuestion(QueryInterface.MODES.MAKE_NO_MISTAKE, this));

        final long startTime = System.currentTimeMillis();

        synchronized (this) {
            r = new Runnable() {
                int counter = 0;

                public void run() {
                    counter++;
                    long timeElapsed = System.currentTimeMillis() - startTime;

                    if (timeElapsed < 5000) {
                        int modulo = 10;
                        if (timeElapsed > 4000) {
                            modulo = 2;
                        } else if (timeElapsed > 3000) {
                            modulo = 5;
                        } else if (timeElapsed > 2000) {
                            modulo = 8;
                        }
                        if (counter % modulo == 0) {
                            //             mediaPlayer.start();
                        }
                        int timeInSecs = (int) (5000 - timeElapsed);
                        progress = 100 * timeInSecs / 5000;
                        progressBar.setProgress(progress);
                        handler.postDelayed(this, 50);
                    } else {
                        onTimeOut();
                    }
                }
            };
            r.run();
        }
    }

    public void refresh() {
        if (r != null) {
            handler.removeCallbacks(r);
        }
        if (builder != null && builder.isShowing()) {
            builder.dismiss();
        }
        stop = false;
    }

    void showScore(GAME_OVER_REASON reason) {

        int rightAnswer = _q.getOptions()[_q.getIdx()];
        loadBanner();
        int topScore = getMnMTopScore();

        boolean isHighScore = rightAnswerCount > topScore;
        String title = MessagingUtils.getDialogueTitle(reason, isHighScore, rightAnswerCount);
        String rightAnswerText = MessagingUtils.getRightAnswerText(reason, isHighScore, rightAnswerCount, rightAnswer);

        String scoreText = MessagingUtils.getScoreText(rightAnswer, isHighScore, rightAnswerCount);

        String message = MessagingUtils.getFunnyMessage(reason, isHighScore, rightAnswerCount, rightAnswer);

        HashMap<String, Object> profileProps = new HashMap<String, Object>();
        if (isHighScore) {
            setMnMTopScore(rightAnswerCount);
            profileProps.put("High Score", rightAnswerCount);
        }
        setHistogramValues(rightAnswerCount);
        profileProps.put("Last Score", topScore);
        cleverTapAPI.profile.push(profileProps);

        HashMap<String, Object> gameEndedProperties = new HashMap<String, Object>();
        gameEndedProperties.put("Score", rightAnswerCount);
        cleverTapAPI.event.push("Game Ended", gameEndedProperties);
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
        TextView titletextView = (TextView) dialoglayout.findViewById(R.id.titletextView);
        TextView rightAnswerTextView = (TextView) dialoglayout.findViewById(R.id.rightAnswerTextView);
        TextView scoreTextView = (TextView) dialoglayout.findViewById(R.id.scoreTextView);
        TextView messageTextView = (TextView) dialoglayout.findViewById(R.id.messageTextView);
        Button back_button = (Button) dialoglayout.findViewById(R.id.back_button);
        back_button.setTextColor(mathster_green);
        Button try_button = (Button) dialoglayout.findViewById(R.id.try_button);
        try_button.setTextColor(mathster_green);
        titletextView.setText(title);


        TextView share = (TextView) dialoglayout.findViewById(R.id.shareMyScore);
        if (isHighScore) {
            share.setVisibility(View.VISIBLE);
            share.setTextColor(mathster_orange);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Screen","Play");
                    cleverTapAPI.event.push("Share Clicked",map);
                    btnShareOnClick();
                }
            });
        } else {
            share.setVisibility(View.INVISIBLE);
        }

        if (rightAnswerText != null) {
            rightAnswerTextView.setText(rightAnswerText);
        } else {
            rightAnswerTextView.setText("");
        }
        if (scoreText != null) {
            scoreTextView.setText(scoreText);
        } else {
            scoreTextView.setText("");
        }
        if (message != null) {
            messageTextView.setText(message);
        } else {
            messageTextView.setText("");
        }

        AlertDialog.Builder bd = new AlertDialog.Builder(this)
                .setView(dialoglayout);
        builder = bd.create();
        //   builder.getWindow().setLayout(1000, 1000);
        builder.setCanceledOnTouchOutside(false);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder != null) {
                    builder.dismiss();
                }
                cleverTapAPI.event.push("Back Pressed");
                onBackPressed();
            }
        });
        try_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cleverTapAPI.event.push("RePlayed");
                HashMap<String, Object> profileProps = new HashMap<String, Object>();
                profileProps.put("Replayed", "true");
                cleverTapAPI.profile.push(profileProps);

                refresh();
                Intent intent = getIntent();
                intent.putExtra("replayed", true);
                finish();
                startActivity(intent);
            }
        });

        refresh();
        builder.show();
        stop = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        refresh();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        refresh();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        QueryInterface.refresh();
        next();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

}
