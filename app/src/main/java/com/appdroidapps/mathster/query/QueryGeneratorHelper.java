package com.appdroidapps.mathster.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pranay on 12/11/15.
 */
public class QueryGeneratorHelper {


    enum Difficulty {

        subzero(1, 4),
        zero(2, 8),
        one(6, 14),
        two(10, 30),
        three(30, 50),
        four(50, 100),
        five(100, 199),
        six(199, 499),
        seven(199, 1000);

        int max, min;

        private Difficulty(int min, int max) {
            this.max = max;
            this.min = min;
        }

    }

    int[] fillers = new int[]{5, 10, 20, 30, 40, 50, 100, 1000, 5000};

    enum Operators {
        add, subtract, multiply, divide
    }

    public interface LevelsInterface {
        Difficulty getFirst();

        Difficulty getSecond();
    }

    public enum AddLevel implements LevelsInterface {
        add1(Difficulty.subzero, Difficulty.zero),
        add2(Difficulty.one, Difficulty.two),
        add3(Difficulty.two, Difficulty.two),
        add4(Difficulty.two, Difficulty.three),
        add5(Difficulty.three, Difficulty.three),
        add6(Difficulty.three, Difficulty.four),
        add7(Difficulty.four, Difficulty.four),
        add8(Difficulty.four, Difficulty.five),
        add9(Difficulty.five, Difficulty.five),
        add10(Difficulty.six, Difficulty.five),
        add11(Difficulty.six, Difficulty.six),
        add12(Difficulty.seven, Difficulty.seven);


        Difficulty first = null;
        Difficulty second = null;

        private AddLevel(Difficulty first, Difficulty second) {
            this.first = first;
            this.second = second;
        }

        public Difficulty getFirst() {
            // TODO Auto-generated method stub
            return first;
        }

        public Difficulty getSecond() {
            // TODO Auto-generated method stub
            return second;
        }

    }

    public enum SubLevel implements LevelsInterface {
        sub1(Difficulty.subzero, Difficulty.zero),
        sub2(Difficulty.one, Difficulty.two),
        sub3(Difficulty.two, Difficulty.two),
        sub4(Difficulty.two, Difficulty.three),
        sub5(Difficulty.three, Difficulty.three),
        sub6(Difficulty.three, Difficulty.four),
        sub7(Difficulty.four, Difficulty.four),
        sub8(Difficulty.four, Difficulty.five),
        sub9(Difficulty.five, Difficulty.five),
        sub10(Difficulty.six, Difficulty.five),
        sub11(Difficulty.six, Difficulty.six),
        sub12(Difficulty.seven, Difficulty.seven);

        Difficulty first = null;
        Difficulty second = null;

        private SubLevel(Difficulty first, Difficulty second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public Difficulty getFirst() {
            // TODO Auto-generated method stub
            return first;
        }

        @Override
        public Difficulty getSecond() {
            // TODO Auto-generated method stub
            return second;
        }
    }

    public enum MulLevel implements LevelsInterface {
        mul1(Difficulty.subzero, Difficulty.zero),
        mul2(Difficulty.one, Difficulty.zero),
        mul3(Difficulty.one, Difficulty.one),
        mul4(Difficulty.two, Difficulty.zero),
        mul5(Difficulty.two, Difficulty.one),
        mul6(Difficulty.three, Difficulty.zero),
        mul7(Difficulty.three, Difficulty.one),
        mul8(Difficulty.four, Difficulty.zero),
        mul9(Difficulty.four, Difficulty.one),
        mul10(Difficulty.four, Difficulty.two),
        mul11(Difficulty.four, Difficulty.three);

        Difficulty first = null;
        Difficulty second = null;

        private MulLevel(Difficulty first, Difficulty second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public Difficulty getFirst() {
            return first;
        }

        @Override
        public Difficulty getSecond() {
            return second;
        }
    }

    public enum DivLevel implements LevelsInterface {
        div1(Difficulty.subzero, Difficulty.zero),
        div2(Difficulty.one, Difficulty.zero),
        div3(Difficulty.one, Difficulty.one),
        div4(Difficulty.two, Difficulty.zero),
        div5(Difficulty.two, Difficulty.one),
        div6(Difficulty.three, Difficulty.zero),
        div7(Difficulty.two, Difficulty.one),
        div8(Difficulty.three, Difficulty.one),
        div9(Difficulty.three, Difficulty.two),
        div10(Difficulty.four, Difficulty.one),
        div11(Difficulty.four, Difficulty.two);

        Difficulty first = null;
        Difficulty second = null;

        private DivLevel(Difficulty first, Difficulty second) {
            this.first = first;
            this.second = second;
        }

        public Difficulty getFirst() {
            return first;
        }

        public Difficulty getSecond() {
            return second;
        }
    }

    public enum UserLevel {
        Level1(AddLevel.add1, 1, SubLevel.sub1, 1, MulLevel.mul1, 1, DivLevel.div1, 0),
        Level2(AddLevel.add3, 1, SubLevel.sub3, 1, MulLevel.mul2, 1, DivLevel.div1, 0),
        Level3(AddLevel.add6, 1, SubLevel.sub5, 1, MulLevel.mul3, 1, DivLevel.div2, 1),
        Level4(AddLevel.add7, 1, SubLevel.sub7, 1, MulLevel.mul5, 1, DivLevel.div5, 1),
        Level5(AddLevel.add8, 1, SubLevel.sub8, 1, MulLevel.mul6, 1, DivLevel.div6, 1),
        Level6(AddLevel.add9, 1, SubLevel.sub9, 1, MulLevel.mul9, 1, DivLevel.div11, 1),
        Level7(AddLevel.add10, 1, SubLevel.sub10, 1, MulLevel.mul10, 1, DivLevel.div11, 1),
        Shakuntala(AddLevel.add11, 1, SubLevel.sub11, 1, MulLevel.mul11, 1, DivLevel.div11, 1),
        Shakuntala2(AddLevel.add12, 1, SubLevel.sub12, 1, MulLevel.mul11, 1, DivLevel.div11, 1);

        AddLevel add;
        SubLevel sub;
        MulLevel mul;
        DivLevel div;
        List<LevelsInterface> list = new ArrayList<>();
        int cadd, csub, cmul, cdiv;

        private UserLevel(AddLevel add, int cadd, SubLevel sub, int csub,
                          MulLevel mul, int cmul, DivLevel div, int cdiv) {
            this.add = add;
            this.sub = sub;
            this.mul = mul;
            this.div = div;
            this.cadd = cadd;
            this.csub = csub;
            this.cmul = cmul;
            this.cdiv = cdiv;
            for (int i = 0; i < cadd; i++) {
                list.add(add);
            }
            for (int i = 0; i < csub; i++) {
                list.add(sub);
            }
            for (int i = 0; i < cmul; i++) {
                list.add(mul);
            }
            for (int i = 0; i < cdiv; i++) {
                list.add(div);
            }
            Collections.shuffle(list);
        }

        private static UserLevel[] vals = values();

        public static UserLevel val(int i) {
            if (i < 0 || i >= vals.length)
                return null;
            return vals[i];
        }
    }


    public static void main(String[] args) throws InterruptedException {
        for (UserLevel l : UserLevel.values()) {
            QuestionsGroup query = new QuestionsGroup(l);
            System.out.println("UserLevel " + l);
            for (int i = 0; i < 20; i++) {

                System.out.println(query.getNextQuestion());
                // Thread.sleep(100);
            }
            System.out.println();

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
        }

    }


   /*
    public enum UserLevelOLD {
        Level1(AddLevel.add1, 1, SubLevel.sub1, 1, MulLevel.mul1, 1, DivLevel.div1, 0),
        Level2(AddLevel.add2, 1, SubLevel.sub2, 1, MulLevel.mul2, 1, DivLevel.div1, 1),
        Level3(AddLevel.add3, 1, SubLevel.sub3, 1, MulLevel.mul3, 1, DivLevel.div3, 1),
        Level4(AddLevel.add4, 1, SubLevel.sub4, 1, MulLevel.mul4, 1, DivLevel.div4, 1),
        Level5(AddLevel.add4, 1, SubLevel.sub4, 1, MulLevel.mul4, 1, DivLevel.div4, 1),
        Shakuntala(AddLevel.add9, 1, SubLevel.sub9, 1, MulLevel.mul9, 1, DivLevel.div11, 1);

        AddLevel add;
        SubLevel sub;
        MulLevel mul;
        DivLevel div;
        List<LevelsInterface> list = new ArrayList<>();
        int cadd, csub, cmul, cdiv;

        private UserLevelOLD(AddLevel add, int cadd, SubLevel sub, int csub,
                             MulLevel mul, int cmul, DivLevel div, int cdiv) {
            this.add = add;
            this.sub = sub;
            this.mul = mul;
            this.div = div;
            this.cadd = cadd;
            this.csub = csub;
            this.cmul = cmul;
            this.cdiv = cdiv;
            for (int i = 0; i < cadd; i++) {
                list.add(add);
            }
            for (int i = 0; i < csub; i++) {
                list.add(sub);
            }
            for (int i = 0; i < cmul; i++) {
                list.add(mul);
            }
            for (int i = 0; i < cdiv; i++) {
                list.add(div);
            }
            Collections.shuffle(list);
        }

        private static UserLevelOLD[] vals = values();

        public static UserLevelOLD val(int i) {
            if (i < 0 || i >= vals.length)
                return null;
            return vals[i];
        }
    }
*/
}
