package com.appdroidapps.mathster.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appdroidapps.mathster.R;
import com.appdroidapps.mathster.utils.DynamicTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pranay on 14/02/16.
 */
public class MultiplicationTablesActivity extends AppCompatActivity {
    private static String LAST_USED_TAB = "LSMT";

    enum PRACTICE_CONTEXT {
        MUL, SQR
    }

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    public Map<Integer, String> tabs = new LinkedHashMap<>();
    private DynamicTextView hideAll, showAll;
    public static PRACTICE_CONTEXT practice_context;
    private static MultiplicationTablesActivity that;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplication_layout);
        that = this;
        hideAll = (DynamicTextView) findViewById(R.id.hideAll);
        showAll = (DynamicTextView) findViewById(R.id.showAll);
        setHideAll();
        int j = 0;
        if (practice_context == PRACTICE_CONTEXT.MUL) {
            practice_context = PRACTICE_CONTEXT.MUL;
            for (int i = 2; i <= 30; i++) {
                tabs.put(j++, String.valueOf(i));
            }
        } else {
            practice_context = PRACTICE_CONTEXT.SQR;
            tabs.put(j++, "Squares");
            tabs.put(j++, "Cubes");
        }

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                int tabPostion = tab.getPosition();
                mViewPager.setCurrentItem(tabPostion);
                if (tabPostion > 0) {
                    RootActivity.setSP(LAST_USED_TAB, tabPostion);
                }
                p = tabPostion;
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }
        };

        for (Integer tabIdx : tabs.keySet()) {
            actionBar.addTab(actionBar.newTab()
                    .setText((tabs.get(tabIdx)))
                    .setTabListener(tabListener));
        }


        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                        if (position > 0) {
                            RootActivity.setSP(LAST_USED_TAB, position);
                        }
                        p = position;
                        // that.setHideAll(positionTTextViewsMap.get(position));
                    }
                });

        if (practice_context == PRACTICE_CONTEXT.MUL) {
            int lastSeenPostion = RootActivity.getSP(LAST_USED_TAB, 6);
            mViewPager.setCurrentItem(lastSeenPostion);
            p = lastSeenPostion;
        } else {
            p = 0;
        }
    }


    public Map<Integer, List<TextView>> positionTTextViewsMap = new HashMap<>();


    class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.POSTION, i);
            fragment.setArguments(args);
            return fragment;
        }

        public int getCount() {
            return tabs.size();
        }

        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }
    }

    public static class DemoObjectFragment extends Fragment {
        public static final String POSTION = "POSITION";

        //  private static Map<Integer, ScrollView> cache = new HashMap<>();
        private View.OnClickListener showAnswer = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                shake.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                    }

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        TextView tv = (TextView) v;

                        tv.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                });
                v.startAnimation(shake);

            }
        };


        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            Bundle args = getArguments();
            int i = args.getInt(POSTION);
            ScrollView scroll = new ScrollView(getContext());
            LinearLayout contain = new LinearLayout(getContext());
            contain.setOrientation(LinearLayout.VERTICAL);
            contain.canScrollVertically(View.FOCUS_DOWN);

            final List<TextView> allViews = new ArrayList<>();

            if (practice_context == PRACTICE_CONTEXT.MUL) {
                int n = i + 2;
                for (int j = 2; j <= 30; j++) {
                    LinearLayout v = (LinearLayout) inflater.inflate(R.layout.row, null);
                    TextView question = (TextView) v.getChildAt(0);
                    question.setText(n + " x " + j);
                    TextView answer = (TextView) ((FrameLayout) v.getChildAt(1)).getChildAt(0);
                    answer.setText((n * j) + "");
                    answer.setOnClickListener(showAnswer);
                    allViews.add(answer);
                    contain.addView(v);
                }
            } else {
                if (i == 0) {
                    for (int j = 2; j <= 30; j++) {

                        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.row, null);
                        TextView question = (TextView) v.getChildAt(0);
                        question.setText(Html.fromHtml(j + "<sup><small> 2 </small></sup>"));
                        TextView answer = (TextView) ((FrameLayout) v.getChildAt(1)).getChildAt(0);
                        answer.setText((j * j) + "");
                        answer.setOnClickListener(showAnswer);
                        allViews.add(answer);
                        contain.addView(v);

                    }
                } else {

                    for (int j = 2; j <= 30; j++) {
                        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.row, null);
                        TextView question = (TextView) v.getChildAt(0);
                        question.setText(Html.fromHtml(j + "<sup><small> 3 </small></sup>"));
                        TextView answer = (TextView) ((FrameLayout) v.getChildAt(1)).getChildAt(0);
                        answer.setText((j * j * j) + "");
                        answer.setOnClickListener(showAnswer);
                        allViews.add(answer);
                        contain.addView(v);


                    }
                }
            }
            scroll.addView(contain);
            //  that.setHideAll(allViews);
            that.positionTTextViewsMap.put(i, allViews);
            return scroll;
        }

    }

    private static int p = 0;

    public void setHideAll() {
        this.hideAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TextView> allViews = positionTTextViewsMap.get(p);
                if (allViews == null) return;
                for (TextView vi : allViews) {

                    vi.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        this.showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TextView> allViews = positionTTextViewsMap.get(p);
                if (allViews == null) return;
                for (TextView vi : allViews) {

                    vi.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }
}
