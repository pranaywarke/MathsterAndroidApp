package com.appdroidapps.mathster.query;

/**
 * Created by pranay on 12/11/15.
 */
public class Question {


    String queryString;
    Integer[] options;
    int idx = -1;
    QueryGeneratorHelper.LevelsInterface lvl;

    boolean answered = false;

    public Question(QueryGeneratorHelper.LevelsInterface lvl, String queryString, Integer[] options, int idx) {
        this.queryString = queryString;
        this.options = options;
        this.idx = idx;
        this.lvl = lvl;
    }


    @Override
    public String toString() {
        return queryString;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Integer[] getOptions() {
        return options;
    }

    public void setOptions(Integer[] options) {
        this.options = options;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
