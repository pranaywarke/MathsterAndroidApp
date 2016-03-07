package com.appdroidapps.mathster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.basegames.BaseGameActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;
import static com.google.android.gms.games.Games.Leaderboards;

// ScoreActity is used BaseGameActivity for Google Api Client Utilites
public class ScoreActivity extends BaseGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public void onSignInFailed() {
        Map<String, Object> map = new HashMap<>();
        map.put("Challenge", RootActivity.context.displayText);
        RootActivity.cleverTapAPI.event.push("SignIn Failed", map);
        makeText(ScoreActivity.this, "Unable to access leaderboard, please try again later.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSignInSucceeded() {
        Map<String, Object> map = new HashMap<>();
        map.put("Challenge", RootActivity.context.displayText);
        RootActivity.cleverTapAPI.event.push("SignIn Succeeded", map);
        showView();
    }


    private String getLeaderBoardString() {
        String leaderBoard = null;
        switch (RootActivity.context) {
            case CHALLENGE_DOS:
                leaderBoard = getString(R.string.leaderboard_mode_duo);
                break;
            case CHALLENGE_TRES:
                leaderBoard = getString(R.string.leaderboard_mode_trio);
                break;
        }
        return leaderBoard;
    }

    private void loadScoreOfLeaderBoard(final Runnable r) {


        Leaderboards.loadCurrentPlayerLeaderboardScore(getApiClient(), getLeaderBoardString(), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            public void onResult(@NonNull final Leaderboards.LoadPlayerScoreResult scoreResult) {
                if (isScoreResultValid(scoreResult)) {

                    final long rank = scoreResult.getScore().getRank();
                    long mPoints = scoreResult.getScore().getRawScore();
                    long topScore = RootActivity.getTopScore();
                    if (mPoints > topScore) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Challenge", RootActivity.context.displayText);
                        RootActivity.cleverTapAPI.event.push("LeaderBoard Synced", map);
                        RootActivity.setTopScore(mPoints);
                        RootActivity.setHistogramValues(mPoints);
                        RootActivity.setLeaderBoardLastUpdatedTopScore(mPoints);
                    }

                    Player holder = scoreResult.getScore().getScoreHolder();
                    if (holder != null) {
                        Map<String, Object> personalInfo = new HashMap<>();
                        String name = null;
                        String displayName = holder.getDisplayName();
                        String playerId = holder.getPlayerId();
                        String uri = holder.getIconImageUrl();
                        if (name != null && name.length() > 1) {
                            personalInfo.put("Name", Character.toUpperCase(name.charAt(0)) + name.substring(1));
                        } else if (displayName != null && displayName.length() > 0) {
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

            long score = RootActivity.getTopScore();
            long l_updated_score = RootActivity.getLeaderBoardLastUpdatedTopScore();
            if (l_updated_score < score && score > 0) {

                Leaderboards.submitScoreImmediate(getApiClient(), getLeaderBoardString(), score);
                RootActivity.setLeaderBoardLastUpdatedTopScore(score);
            }
            final int BOARD_REQUEST_CODE = 1;
            startActivityForResult(Leaderboards.getLeaderboardIntent(getApiClient(), getLeaderBoardString()), BOARD_REQUEST_CODE);
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
