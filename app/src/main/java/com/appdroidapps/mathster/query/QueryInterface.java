package com.appdroidapps.mathster.query;

import android.content.Context;
import android.util.Log;

import com.appdroidapps.mathster.activities.RootActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranay on 12/11/15.
 */
public class QueryInterface {


    public enum MODES {
        MAKE_NO_MISTAKE, MATHSTER_INDEX
    }

    private static Map<MODES, Query> modeQueryMap = new HashMap<MODES, Query>();

    static {
        refresh();
    }

    public static void refresh() {
        for (MODES m : MODES.values()) {

            //     int ordinal = Persistence.getUserLevelOrdinal(m);
            int ordinal = -1;
            QueryGeneratorHelper.UserLevel l = QueryGeneratorHelper.UserLevel.val(ordinal);
            if (l == null) {
                l = QueryGeneratorHelper.UserLevel.Level1;
            }
            modeQueryMap.put(m, new Query(l));
        }
    }

    QueryGeneratorHelper.UserLevel currentLevel = null;


    public static Question getQuestion(MODES mode, Context ctx) {
        Query query = modeQueryMap.get(mode);
        if (isTimeToSwitchLevels(query, mode)) {
            HashMap<String, Object> profileProps = new HashMap<String, Object>();
            int ord = query.l.ordinal();
            if (ord != QueryGeneratorHelper.UserLevel.values().length - 1) {
                ord++;
                query = new Query(QueryGeneratorHelper.UserLevel.val(ord));
                modeQueryMap.put(mode, query);
            }
            profileProps.put("Level",query.l.name());
            RootActivity.cleverTapAPI.profile.push(profileProps);
        }
        return query.getNextQuestion();
    }

    private static boolean isTimeToSwitchLevels(Query query, MODES mode) {
        int add = query.add;
        int sub = query.sub;
        int mul = query.mul;
        int div = query.div;

        switch (mode) {
            case MAKE_NO_MISTAKE:
                switch (query.l) {
                    case Level1:
                        return add > 2 && sub > 2 && mul > 1;
                    case Level2:
                        return add > 1 && sub > 1 && mul > 1;
                    case Level3:
                        return add > 2 && sub > 2 && mul > 1;
                    case Level4:
                        return add > 1 && sub > 1 && mul > 1;
                    case Level5:
                        return add > 2 && sub > 2 && mul > 1 && div > 2;

                }
                return add > 1 && sub > 1 && mul > 1;

            case MATHSTER_INDEX:
                return add > 5 && sub > 3 && mul > 2;
        }
        return false;
    }


    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        while (true) {
            Thread.sleep(10);

            Question q = getQuestion(MODES.MAKE_NO_MISTAKE, null);
            //    if(System.currentTimeMillis()%2==0){
            q.setAnswered(true);
            //  }
            System.out.println("Question " + i++ + ":    " + q.getQueryString());
            System.out.println();
            //       System.out.println("Options :    " + Arrays.toString(q.getOptions()));
        }
    }
}
