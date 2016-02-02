package com.appdroidapps.mathster.query;

import com.appdroidapps.mathster.activities.RootActivity;

import java.util.HashMap;
import java.util.Map;

import static com.appdroidapps.mathster.activities.RootActivity.GameContext;

/**
 * Created by pranay on 12/11/15.
 */
public class QueryInterface {


    private static Map<GameContext, QuestionsGroup> modeQueryMap = new HashMap<>();

    static {
        refresh();
    }

    public static void refresh() {
        for (GameContext m : GameContext.values()) {
            int ordinal = -1;
            QueryGeneratorHelper.UserLevel l = QueryGeneratorHelper.UserLevel.val(ordinal);
            if (l == null) {
                l = QueryGeneratorHelper.UserLevel.Level1;
            }
            modeQueryMap.put(m, new QuestionsGroup(l));
        }
    }

    QueryGeneratorHelper.UserLevel currentLevel = null;


    public static Question getQuestion() {
        GameContext mode = RootActivity.context;
        QuestionsGroup query = modeQueryMap.get(mode);
        if (isTimeToSwitchLevels(query, mode)) {
            HashMap<String, Object> profileProps = new HashMap();
            int ord = query.userLevel.ordinal();
            if (ord != QueryGeneratorHelper.UserLevel.values().length - 1) {
                ord++;
                query = new QuestionsGroup(QueryGeneratorHelper.UserLevel.val(ord));
                modeQueryMap.put(mode, query);
            }
            profileProps.put("Level", query.userLevel.name());
            System.out.println("Levels changed");
            //TODO uncomment
            //   cleverTapAPI.profile.push(profileProps);
        }
        return query.getNextQuestion();
    }

    private static boolean isTimeToSwitchLevels(QuestionsGroup query, GameContext mode) {
        int add = query.add;
        int sub = query.sub;
        int mul = query.mul;
        int div = query.div;

        switch (mode) {
            case CHALLENGE_DOS:
                switch (query.userLevel) {
                    case Level1:
                        return add > 2 && sub > 2 && mul > 1;
                    case Level2:
                        return add > 1 && sub > 1 && mul > 1;
                    case Level3:
                        return add > 2 && sub > 2 && mul > 1 && div > 1;
                    case Level4:
                        return add > 1 && sub > 1 && mul > 1 && div > 1;
                    case Level5:
                        return add > 2 && sub > 2 && mul > 2 && div > 2;
                    case Level6:
                    case Level7:
                    case Shakuntala:
                        return add > 2 && sub > 2 && mul > 2 && div > 2;

                }
            case CHALLENGE_TRES:
                switch (query.userLevel) {
                    case Level1:
                        return add > 3 && sub > 3 && mul > 1;
                    case Level2:
                        return add > 2 && sub > 2 && mul > 2;
                    case Level3:
                        return add > 2 && sub > 2 && mul > 2;
                    case Level4:
                        return add > 2 && sub > 2 && mul > 2;
                    case Level5:
                        return add > 2 && sub > 2 && mul > 2;
                    case Level6:
                    case Level7:
                    case Shakuntala:
                        return add > 2 && sub > 2 && mul > 2;
                }

        }
        return false;
    }


    public static void main(String[] args) throws InterruptedException {
        print(GameContext.CHALLENGE_DOS);
        System.out.println("----------");
        print(GameContext.CHALLENGE_TRES);
    }

    private static void print(GameContext c) {
        QueryInterface.refresh();
        RootActivity.context = c;

        int j = 0;
        while (true) {
            if (j++ > 150) {
                break;
            }
            Question q = getQuestion();
            q.setAnswered(true);
            System.out.println("Question Number : " + j);
            System.out.println(q.getQueryString());
            for (Integer i : q.getOptions()) {
                System.out.println(i);
            }
            System.out.println();
        }
    }
}
