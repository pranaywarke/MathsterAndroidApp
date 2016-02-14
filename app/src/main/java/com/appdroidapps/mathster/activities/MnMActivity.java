package com.appdroidapps.mathster.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.query.QueryInterface;
import com.appdroidapps.mathster.utils.AchievementsUtil;
import com.appdroidapps.mathster.utils.Falcon;
import com.appdroidapps.mathster.utils.MessagingUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;

/**
 * Created by pranay on 14/11/15.
 */

public class MnMActivity extends PlayFieldActivity {


    private static final String images_folder_name = "shared_images";
    private static final String image_name = "image.png";
    protected static Runnable r = null;

    protected int progress;
    private AlertDialog builder;
    protected boolean stop = false;
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
            currentScore = currentScore + 3;
        } else if (progress > 33) {
            bonusScore.setText("+2 XP");
            currentScore = currentScore + 2;
        } else {
            bonusScore.setText("");
        }
        vibrator.vibrate(25);
        if (currentHighScore > 0) {
            int left = currentHighScore - currentScore;
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


    protected void next() {
        currentScoreTextView.setText("" + currentScore);
        showQuestion(QueryInterface.getQuestion());

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
        int topScore = getTopScore();

        boolean isHighScore = currentScore > topScore;
        String title = MessagingUtils.getDialogueTitle(reason, isHighScore, currentScore);
        String rightAnswerText = MessagingUtils.getRightAnswerText(reason, isHighScore, currentScore, rightAnswer);

        String scoreText = MessagingUtils.getScoreText(rightAnswer, isHighScore, currentScore);

        String message = MessagingUtils.getFunnyMessage(reason, isHighScore, currentScore, rightAnswer);

        HashMap<String, Object> profileProps = new HashMap<>();
        if (isHighScore) {
            setTopScore(currentScore);
            profileProps.put("High Score", currentScore);
        }
        setHistogramValues(currentScore);
        profileProps.put("Last Score", topScore);
        cleverTapAPI.profile.push(profileProps);

        HashMap<String, Object> gameEndedProperties = new HashMap<>();
        gameEndedProperties.put("Score", currentScore);
        gameEndedProperties.put("Challenge", RootActivity.context.displayText);
        cleverTapAPI.event.push("Game Ended", gameEndedProperties);
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
        TextView titletextView = (TextView) dialoglayout.findViewById(R.id.titletextView);
        TextView rightAnswerTextView = (TextView) dialoglayout.findViewById(R.id.rightAnswerTextView);
        TextView scoreTextView = (TextView) dialoglayout.findViewById(R.id.scoreTextView);
        TextView messageTextView = (TextView) dialoglayout.findViewById(R.id.messageTextView);
        Button back_button = (Button) dialoglayout.findViewById(R.id.back_button);
        back_button.setTextColor(ContextCompat.getColor(this, R.color.mathster3));
        Button try_button = (Button) dialoglayout.findViewById(R.id.try_button);
        try_button.setTextColor(ContextCompat.getColor(this, R.color.mathster3));
        titletextView.setText(title);


        TextView share = (TextView) dialoglayout.findViewById(R.id.shareMyScore);
        if (currentScore > 0) {
            share.setVisibility(View.VISIBLE);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String mPath = Environment.getExternalStorageDirectory().toString() + "/mathster_highscore.jpg";
                        File imageFile = new File(mPath);
                        Falcon.takeScreenshot(MnMActivity.this, imageFile);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
                        startActivity(Intent.createChooser(share, "Share Screenshot"));
                        Map<String, Object> map = new HashMap<>();
                        map.put("Screen", "Play");
                        map.put("Challenge", RootActivity.context.displayText);
                        cleverTapAPI.event.push("Image Shared", map);
                    } catch (Exception e) {
                        makeText(MnMActivity.this, "Unable to share screenshot.", Toast.LENGTH_SHORT).show();

                    }
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
                Map<String, Object> map = new HashMap<>();
                map.put("Challenge", RootActivity.context.displayText);
                cleverTapAPI.event.push("Back Pressed", map);
                onBackPressed();
            }
        });
        try_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("Challenge", RootActivity.context.displayText);
                cleverTapAPI.event.push("RePlayed", map);
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
        AchievementsUtil.updateAchievements(currentScore);
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
