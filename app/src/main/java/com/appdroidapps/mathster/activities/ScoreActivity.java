package com.appdroidapps.mathster.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.basegames.BaseGameActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.makeText;
import static com.google.android.gms.games.Games.Leaderboards;

// ScoreActity is used BaseGameActivity for Google Api Client Utilites
public class ScoreActivity extends BaseGameActivity {

    public static final String ACTION_VIEW_LEADERBOARD = "view_leaderboard";
    private Handler handler = new Handler();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.intent = intent;
        super.onNewIntent(intent);
    }

    @Override
    public void onSignInFailed() {
        RootActivity.cleverTapAPI.event.push("SignIn Failed");
        makeText(ScoreActivity.this, "Unable to access leaderboard, please try again later.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSignInSucceeded() {
        RootActivity.cleverTapAPI.event.push("SignIn Succeeded");
        showView();
    }

    private void loadScoreOfLeaderBoard(final Runnable r) {


        Leaderboards.loadCurrentPlayerLeaderboardScore(getApiClient(), getString(R.string.leaderboard), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                if (isScoreResultValid(scoreResult)) {

                    final long rank = scoreResult.getScore().getRank();
                    long mPoints = scoreResult.getScore().getRawScore();
                    long topScore = RootActivity.getMnMTopScore();
                    if (mPoints > topScore) {
                        RootActivity.cleverTapAPI.event.push("LeaderBoard Synced");
                        RootActivity.setMnMTopScore(mPoints);
                        RootActivity.setHistogramValues(mPoints);
                        RootActivity.setLeaderBoardLastUpdatedTopScore(mPoints);
                    }

                    Player holder = scoreResult.getScore().getScoreHolder();
                    if (holder != null) {
                        Map<String, Object> personalInfo = new HashMap<String, Object>();
                        String name = holder.getName();
                        String displayName = holder.getDisplayName();
                        String playerId = holder.getPlayerId();
                        String uri = holder.getIconImageUrl();
                        if (name != null && name.length() > 1) {
                            personalInfo.put("Name", Character.toUpperCase(name.charAt(0)) + name.substring(1));
                        }else if (displayName != null && displayName.length() > 0) {
                            personalInfo.put("Name", displayName);
                        }
                        if (playerId != null) {
                            personalInfo.put("Player Id", playerId);
                        }
                        if (uri != null) {
                            personalInfo.put("Photo", uri);
                        }
                        RootActivity.cleverTapAPI.profile.push(personalInfo);
                    }

                    RootActivity.setLeaderBoardSynced(System.currentTimeMillis());

                    r.run();
                    // get Total number of players here to calculate percentiles

                 /*   Leaderboards.loadLeaderboardMetadata(getApiClient(),getString(R.string.leaderboard),true).setResultCallback(new ResultCallback<com.google.android.gms.games.leaderboard.Leaderboards.LeaderboardMetadataResult>() {
                        public void onResult(Leaderboards.LeaderboardMetadataResult leaderboardMetadataResult) {
                            int total = leaderboardMetadataResult.getLeaderboards().getCount();
                            int percentile = (int) (((total - rank) / total) * 100);
                            Toast.makeText(ScoreActivity.this, "Pecentile "+percentile, Toast.LENGTH_LONG).show();
                            r.run();
                        }
                    });*/


                } else {
                    r.run();
                }
            }
        });

    }

    private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
        return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
    }


    private Runnable run = new Runnable() {
        public void run() {

            long score = RootActivity.getMnMTopScore();
            long l_updated_score = RootActivity.getLeaderBoardLastUpdatedTopScore();
            if (l_updated_score < score && score > 0) {

                Leaderboards.submitScoreImmediate(getApiClient(),
                        getString(R.string.leaderboard),
                        score);
                RootActivity.setLeaderBoardLastUpdatedTopScore(score);
            }
            final int BOARD_REQUEST_CODE = 1;
            startActivityForResult(Leaderboards.getLeaderboardIntent(
                    getApiClient(), getString(R.string.leaderboard)), BOARD_REQUEST_CODE);
            finish();
        }
    };

    private void showView() {

        boolean isLeaderBoardSynced = RootActivity.isLeaderBoardSynced();
        if (!isLeaderBoardSynced) {
            loadScoreOfLeaderBoard(run);
        } else {
            run.run();
        }

    }
}
