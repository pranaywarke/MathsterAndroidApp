package com.appdroidapps.mathster.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by pranay on 12/11/15.
 */
public class Query {


    private HashSet<String> repeat = new HashSet<>();
    int add = 0;
    int sub = 0;
    int mul = 0;
    int div = 0;

    Question previousQuestion = null;

    QueryGeneratorHelper.UserLevel l;
    String query;
    int current;

    public Query(QueryGeneratorHelper.UserLevel l) {
        this.l = l;
    }

    MyRandom left = new MyRandom();
    MyRandom right = new MyRandom();


    class MyRandom extends Random {

        int lastGenerated = -1;

        public int nextInt() {
            int x = super.nextInt();
            if (x != lastGenerated) {
                lastGenerated = x;
                return x;
            } else {
                return nextInt();
            }
        }

        public int nextInt(int n) {
            int x = super.nextInt(n);
            if (x != lastGenerated) {
                lastGenerated = x;
                return x;
            } else {
                return nextInt(n);
            }
        }
    }

    Question getNextQuestion() {
        return getNextQuestion(0);
    }

    private Question getNextQuestion(int counter) {

        if (previousQuestion != null && previousQuestion.isAnswered()) {
            if (previousQuestion.lvl instanceof QueryGeneratorHelper.AddLevel) {
                add++;
            } else if (previousQuestion.lvl instanceof QueryGeneratorHelper.SubLevel) {
                sub++;
            } else if (previousQuestion.lvl instanceof QueryGeneratorHelper.MulLevel) {
                mul++;
            } else if (previousQuestion.lvl instanceof QueryGeneratorHelper.DivLevel) {
                div++;
            }
        }
        if (current == l.list.size()) {
            current = 0;
        }
        QueryGeneratorHelper.LevelsInterface lvl = l.list.get(current++);

        int x = 0, y = 0;
        while (x == 0) {
            x = left.nextInt(lvl.getFirst().max - lvl.getFirst().min)
                    + lvl.getFirst().min;
        }
        while (y == 0 ||
                (x == y && (lvl instanceof QueryGeneratorHelper.DivLevel || lvl instanceof QueryGeneratorHelper.SubLevel))) {
            y = right.nextInt(lvl.getSecond().max - lvl.getSecond().min)
                    + lvl.getSecond().min;
        }
        if ((lvl instanceof QueryGeneratorHelper.DivLevel || lvl instanceof QueryGeneratorHelper.SubLevel) && x < y) {
            x = x + y;
            y = x - y;
            x = x - y;
        }
        int rightAnswer = -1;
        String text = "";
        if (lvl instanceof QueryGeneratorHelper.AddLevel) {
            text = x + " + " + y;
            rightAnswer = x + y;
        } else if (lvl instanceof QueryGeneratorHelper.SubLevel) {
            text = x + " - " + y;
            rightAnswer = x - y;
        } else if (lvl instanceof QueryGeneratorHelper.MulLevel) {
            text = x + " x " + y;
            rightAnswer = x * y;
        } else if (lvl instanceof QueryGeneratorHelper.DivLevel) {
            text = x * y + " / " + y;
            rightAnswer = x;
        }

        int idx = left.nextInt(4);
        previousQuestion = new Question(lvl, text, getOptions(rightAnswer, idx), idx);
        if (repeat.contains(previousQuestion.getQueryString()) && counter < 20) {
            return getNextQuestion(++counter);
        }
        repeat.add(previousQuestion.getQueryString());
        return previousQuestion;
    }


    private static Integer[] getOptions(int rightAnswer, int idx) {
        Random random = new Random();
        Set<Integer> answers = new HashSet<Integer>();
        int iters = 100;
        while (answers.size() < 4) {

            if (rightAnswer < 10) {
                int na = random.nextInt(10);
                if (na != rightAnswer) {
                    answers.add(na);
                }
            } else {
                int lastDigit = rightAnswer % 10;
                int firstDigit = rightAnswer / 10;

                while (answers.size() < 4) {
                    int _f = firstDigit++ % 10;
                    if (rightAnswer > 100) {
                        _f = firstDigit++ % 100;
                    }
                    String _d = _f + "" + lastDigit;
                    int na = Integer.parseInt(_d);
                    if (na != rightAnswer) {
                        answers.add(na);
                    }

                }

            }
        }
        Integer[] options = new Integer[]{0, 0, 0, 0};
        answers.toArray(options);
        options[idx] = rightAnswer;
        return options;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(getOptions(999, 2)));
        System.out.println(Arrays.toString(getOptions(11, 3)));
        System.out.println(Arrays.toString(getOptions(12, 1)));
        System.out.println(Arrays.toString(getOptions(13, 1)));
        System.out.println(Arrays.toString(getOptions(14, 2)));
        System.out.println(Arrays.toString(getOptions(15, 3)));
        System.out.println(Arrays.toString(getOptions(16, 2)));
        System.out.println(Arrays.toString(getOptions(17, 1)));

    }

}
