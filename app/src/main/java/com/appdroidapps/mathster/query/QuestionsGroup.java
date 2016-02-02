package com.appdroidapps.mathster.query;

import com.appdroidapps.mathster.activities.RootActivity;
import com.appdroidapps.mathster.query.QueryGeneratorHelper.LevelsInterface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by pranay on 12/11/15.
 */
public class QuestionsGroup {


    int[] OneToTwo = new int[]{2, 1};
    int[] OneToFive = new int[]{1, 2, 10};
    int[] OneToT = new int[]{10, 20, 30};


    private HashSet<String> repeat = new HashSet<>();
    int add = 0;
    int sub = 0;
    int mul = 0;
    int div = 0;

    Question previousQuestion = null;

    QueryGeneratorHelper.UserLevel userLevel;
    String query;
    int current;

    public QuestionsGroup(QueryGeneratorHelper.UserLevel userLevel) {

        this.userLevel = userLevel;
        current = 0;
    }

    MyRandom left = new MyRandom();
    MyRandom right = new MyRandom();

    public int getZfor(QueryGeneratorHelper.UserLevel level) {
        if (level.ordinal() < 2) {
            return OneToTwo[left.nextInt(OneToTwo.length)];
        }
        if (level.ordinal() < 3) {
            return OneToFive[left.nextInt(OneToFive.length)];
        }
        return OneToT[left.nextInt(OneToT.length)];
    }

    public static class MyRandom extends Random {

        int lastGenerated = -1;


        public int nextIntR() {
            return nextInt(0);
        }

        public int nextIntR(int counter) {
            int x = super.nextInt();
            if (x != lastGenerated || counter > 3) {
                lastGenerated = x;
                return x;
            } else {
                return nextIntR(++counter);
            }
        }

        public int nextInt(int n) {
            return nextInt(n, 0);
        }

        public int nextInt(int n, int counter) {
            int x = super.nextInt(n);
            if (x != lastGenerated || counter > 3) {
                lastGenerated = x;
                return x;
            } else {
                return nextInt(n, ++counter);
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

        Object[] _rAT = null;
        switch (RootActivity.context) {
            case CHALLENGE_DOS:
                _rAT = getTextAndRightAnswer(left, right);
                break;
            case CHALLENGE_TRES:
                _rAT = getTextAndRightAnswerForMode2(left, right);
                break;
        }


        int rightAnswer = (Integer) _rAT[1];
        String text = (String) _rAT[0];


        int idx = left.nextInt(4);
        previousQuestion = new Question((LevelsInterface) _rAT[2], text, getOptions(rightAnswer, idx), idx);
        if (repeat.contains(previousQuestion.getQueryString()) && counter < 20) {
            return getNextQuestion(++counter);
        }
        repeat.add(previousQuestion.getQueryString());
        return previousQuestion;
    }


    private LevelsInterface getLevel() {
        if (current == userLevel.list.size()) {
            current = 0;
        }
        return userLevel.list.get(current++);
    }

    private Object[] getTextAndRightAnswerForMode2(MyRandom left, MyRandom right) {
        LevelsInterface lvl = getLevel();
        if (lvl instanceof QueryGeneratorHelper.DivLevel) {
            return getTextAndRightAnswerForMode2(left, right);
        }
        Object[] ret = new Object[3];
        int x = 0, y = 0, z = 0;
        while (x == 0) {
            x = left.nextInt(lvl.getFirst().max - lvl.getFirst().min)
                    + lvl.getFirst().min;
        }
        while (y == 0 ||
                (x == y && (lvl instanceof QueryGeneratorHelper.SubLevel))) {
            y = right.nextInt(lvl.getSecond().max - lvl.getSecond().min)
                    + lvl.getSecond().min;
        }

        if (lvl instanceof QueryGeneratorHelper.SubLevel && x < y) {
            x = x + y;
            y = x - y;
            x = x - y;
        }

        if (lvl instanceof QueryGeneratorHelper.AddLevel) {
            if (left.nextInt(2) == 1) {
                z = left.nextInt(lvl.getFirst().max - lvl.getFirst().min) + lvl.getFirst().min;
                ret[0] = x + " + " + y + " + " + z;
                ret[1] = x + y + z;
            } else {
                z = (left.nextInt(x + y));
                ret[0] = x + " + " + y + " - " + z;
                ret[1] = x + y - z;
            }
        } else if (lvl instanceof QueryGeneratorHelper.SubLevel) {
            if (left.nextInt(2) == 1) {
                z = left.nextInt(lvl.getFirst().max - lvl.getFirst().min) + lvl.getFirst().min;
                ret[0] = x + " - " + y + " + " + z;
                ret[1] = x - y + z;
            } else {
                z = (left.nextInt(x - y));
                ret[0] = x + " - " + y + " - " + z;
                ret[1] = x - y - z;
            }
        } else if (lvl instanceof QueryGeneratorHelper.MulLevel) {
            z = getZfor(userLevel);
            if (z == 0) z = 1;
            ret[0] = x + " x " + y + " x " + z;
            ret[1] = x * y * z;
        }
        ret[2] = lvl;
        return ret;
    }

    private Object[] getTextAndRightAnswer(MyRandom left, MyRandom right) {
        Object[] ret = new Object[3];
        LevelsInterface lvl = getLevel();

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

        if (lvl instanceof QueryGeneratorHelper.AddLevel) {
            ret[0] = x + " + " + y;
            ret[1] = x + y;
        } else if (lvl instanceof QueryGeneratorHelper.SubLevel) {
            ret[0] = x + " - " + y;
            ret[1] = x - y;
        } else if (lvl instanceof QueryGeneratorHelper.MulLevel) {
            ret[0] = x + " x " + y;
            ret[1] = x * y;
        } else if (lvl instanceof QueryGeneratorHelper.DivLevel) {
            ret[0] = x * y + " \u00F7 " + y;
            ret[1] = x;
        }
        ret[2] = lvl;
        return ret;
    }

    private static Integer[] getOptions(int rightAnswer, int idx) {
        MyRandom random = new MyRandom();
        Set<Integer> answers = new HashSet<Integer>();
        long ts = System.nanoTime();


        int firstDigits = rightAnswer / 10;
        int lastDigit = rightAnswer % 10;

        int max = ((firstDigits / 10) + 1) * 10;
        int min = ((firstDigits / 10)) * 10;
        String _d = null;
        int d = -1, na = -1;
        while (answers.size() < 4) {
            if (rightAnswer < 10) {
                na = random.nextInt(10);
            } else {
                d = (random.nextInt(max - min) + min);

                if (d == 0) continue;
                _d = d + "" + lastDigit;
                na = Math.abs(Integer.parseInt(_d));
            }

            if (na != rightAnswer) {
                answers.add(na);
            }
        }
        Integer[] options = new Integer[]{0, 0, 0, 0};
        answers.toArray(options);
        options[idx] = rightAnswer;
        return options;
    }

//    private static Integer[] getOptions(int rightAnswer, int idx) {
//        Random random = new Random();
//        Set<Integer> answers = new HashSet<Integer>();
//        long ts = System.nanoTime();
//
//        while (answers.size() < 4) {
//
//            if (rightAnswer < 10) {
//                int na = random.nextInt(10);
//                if (na != rightAnswer) {
//                    answers.add(na);
//                }
//            } else {
//
//                int lastDigit = rightAnswer % 10;
//                int firstDigits = rightAnswer / 10;
//
//
//                while (answers.size() < 4) {
//                    //    int _f = firstDigit++ % den;
//                    int f = -1;
//                    if (ts % 3 == 0) {
//                        f = random.nextInt(firstDigits + 10 - (firstDigits - 10)) + firstDigits - 10;
//                    } else if (ts % 4 == 0) {
//                        f = firstDigits--;
//                    } else if (ts % 5 == 0) {
//                        f = firstDigits++;
//                    } else {
//                        f = random.nextInt(firstDigits + 10 - (firstDigits - 10)) + firstDigits - 10;
//                    }
//                    if (f == 0) {
//                        continue;
//                    }
//                    String _d = f + "" + lastDigit;
//                    int na = Math.abs(Integer.parseInt(_d));
//                    if (na != rightAnswer) {
//                        answers.add(na);
//                    }
//
//                }
//
//            }
//        }
//        Integer[] options = new Integer[]{0, 0, 0, 0};
//        answers.toArray(options);
//        options[idx] = rightAnswer;
//        return options;
//    }

    public static void main(String[] args) {
//        System.out.println(Arrays.toString(getOptions(99982, 2)));
//        System.out.println(Arrays.toString(getOptions(11364, 3)));
//        System.out.println(Arrays.toString(getOptions(1232, 1)));
//        System.out.println(Arrays.toString(getOptions(13333, 1)));
//        System.out.println(Arrays.toString(getOptions(124, 2)));
//        System.out.println(Arrays.toString(getOptions(15, 3)));
//        System.out.println(Arrays.toString(getOptions(16, 2)));
        System.out.println(Arrays.toString(getOptions(100, 0)));
        int i = 0;
        while (i++ < 1000000) {
            System.out.println(Arrays.toString(getOptions(i, 0)));
        }


    }

}
