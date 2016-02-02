package com.appdroidapps.mathster.utils;

import com.appdroidapps.mathster.activities.PlayFieldActivity;
import com.appdroidapps.mathster.activities.RootActivity;

import java.util.Calendar;

/**
 * Created by pranay on 17/01/16.
 */
public class MessagingUtils {

    static String WELCOME_MS_KEY = "last_time_wel";
    static String HIGH_SCORE_KEY = "last_highscore_set";

    public static String getWelcomeMessage() {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String name = RootActivity.cleverTapAPI.profile.getProperty("Name");
        String _ts = RootActivity.getFromSharedPrefrences(WELCOME_MS_KEY);
        int diffInHours = -1;
        long highScore = RootActivity.getTopScore();
        if (_ts != null) {
            diffInHours = (int) ((System.currentTimeMillis() - Long.parseLong(_ts)) / 3600000);
        }
        if (diffInHours != -1 && diffInHours < 4) {
            return null;
        }
        String greeting = null;
        boolean late = false;

        if (hourOfDay < 12) {
            greeting = "Good Morning! ";
        } else if (hourOfDay < 17) {
            greeting = "Good Afternoon! ";
        } else {
            greeting = "Good Evening! ";
        }

        if (name == null) {
            name = "Buddy";
        }
        String beatMessage = "";
        if (highScore > 0) {
            //        beatMessage = ", Let's try to beat " + highScore + " this time";
        }
        String finalMessage = greeting + name + beatMessage;
        RootActivity.setToSharedPreferences(WELCOME_MS_KEY, String.valueOf(System.currentTimeMillis()));
        return finalMessage;
    }

    public static String getDialogueTitle(PlayFieldActivity.GAME_OVER_REASON reason, boolean isHighScore, int score) {
        String text = "Mathster";
        String _ts = RootActivity.getFromSharedPrefrences(HIGH_SCORE_KEY);
        int diffInHours = -1;
        long highScore = RootActivity.getTopScore();
        if (_ts != null) {
            diffInHours = (int) ((System.currentTimeMillis() - Long.parseLong(_ts)) / 3600000);
        }
        if (isHighScore) {
            if (diffInHours != -1 && diffInHours < 10) {
                text = "You're on a roll! Another high score";
            } else {
                text = "Woohoo! That's your best score";
            }
        } else {
            switch (reason) {
                case TIMEOUT:
                    text = "Oops! Timed out";
                    break;
                case WRONG_ANSWER:
                    text = "Nah! That's wrong";
                    break;
            }
        }
        RootActivity.setToSharedPreferences(HIGH_SCORE_KEY, String.valueOf(System.currentTimeMillis()));
        return text;
    }

    public static String getScoreText(int rightAnswer, boolean isHighScore, int score) {
        return "You scored " + score;
    }

    public static String getFunnyMessage(PlayFieldActivity.GAME_OVER_REASON reason, boolean isHighScore, int rightAnswerCount, int rightAnswer) {
        String text = null;
        String name = RootActivity.cleverTapAPI.profile.getProperty("Name");
        if (name == null) name = "buddy";
        if (!isHighScore) {
            Long scoreOftheay = RootActivity.getScoreOfTheDay();
            if (scoreOftheay != null && rightAnswerCount > scoreOftheay) {
                text = "That's your best score for the day!";
                return text;
            }
        } else {
            text = "Way to go " + name + "! You should let your friends know";
            return text;
        }
        int topScore = RootActivity.getTopScore();
        if (rightAnswerCount > 100 && rightAnswerCount > 0.75 * topScore) {
            return "That was pretty close to your top score " + name + "! You should try again";
        }
        switch (reason) {
            case TIMEOUT:
                text = "Just a tad faster " + name;
                break;
            case WRONG_ANSWER:
                if (rightAnswerCount < 50) {
                    //   text = "Concentrate " + name + " concentrate!";
                } else {
                    //     if (System.currentTimeMillis() % 2 == 0)
                    //         text = "Was it the bonus points you were going for";
                    //     else
                    text = "You should try giving it another shot";
                    // }
                }
                break;
        }
        return text;
    }

    public static String getRightAnswerText(PlayFieldActivity.GAME_OVER_REASON reason, boolean isHighScore, int rightAnswerCount, int rightAnswer) {

        if (isHighScore) {
            return "Anyways! Should have answered " + rightAnswer + " \uD83D\uDE1C";
        } else {
            return "Should have answered " + rightAnswer + " \uD83D\uDE1C";
        }
    }
}
