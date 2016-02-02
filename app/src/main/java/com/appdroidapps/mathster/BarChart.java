package com.appdroidapps.mathster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appdroidapps.mathster.utils.MultitouchView;

import java.util.ArrayList;

/**
 * Created by PanKaj on 1/10/2016.
 */
public class BarChart extends MultitouchView {
    private final Context context;
    public static final int[] colors = new int[]{
            Color.parseColor("#794044"),
            Color.parseColor("#114355"),
            Color.parseColor("#009f6a"),
            Color.parseColor("#ab9c73"),
            Color.parseColor("#daa520"),
            Color.parseColor("#31698a"),
            Color.parseColor("#d4383a"),
            Color.parseColor("#794044"),
            Color.parseColor("#794044"),
            Color.parseColor("#114355"),
            Color.parseColor("#009f6a"),
            Color.parseColor("#ab9c73"),
            Color.parseColor("#daa520"),
            Color.parseColor("#31698a"),
            Color.parseColor("#d4383a"),
            Color.parseColor("#794044"),
            Color.parseColor("#794044"),
            Color.parseColor("#114355"),
            Color.parseColor("#009f6a"),
            Color.parseColor("#ab9c73"),
            Color.parseColor("#daa520"),
            Color.parseColor("#31698a"),
            Color.parseColor("#d4383a"),
            Color.parseColor("#794044"),
            Color.parseColor("#794044"), Color.parseColor("#114355"), Color.parseColor("#009f6a"), Color.parseColor("#ab9c73"), Color.parseColor("#daa520"), Color.parseColor("#31698a"), Color.parseColor("#d4383a"), Color.parseColor("#794044")};

    private ArrayList<BarData> bars_list;

    public BarChart(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public BarChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    private TextView addScoreView(LinearLayout layout, String text) {

        TextView text_view = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        text_view.setLayoutParams(params);
        text_view.setText(text);
        text_view.setSingleLine(true);
        text_view.setGravity(Gravity.CENTER);
        text_view.setTypeface(Typeface.DEFAULT_BOLD);
        //     text_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        //     text_view.setPadding(2, 2, 2, 2);
        text_view.setEllipsize(TextUtils.TruncateAt.END);
        //  text_view.setShadowLayer(1, 0, 0, Color.BLACK);
        layout.addView(text_view, 0);
        return text_view;
    }

    private View addBarView(LinearLayout layout, int idx) {
        View view = new View(context);

        view.setBackgroundColor(colors[idx]);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 8;
        view.setLayoutParams(params);
        layout.addView(view, 1);
        return view;
    }

    private TextView addLabelView(LinearLayout layout, String label) {
        TextView label_view = new TextView(context);
        label_view.setText(label);
        label_view.setGravity(Gravity.CENTER);
        //  label_view.setSingleLine(true);
        //        label_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        //     label_view.setPadding(1, 2, 2, 2);
        //    label_view.setShadowLayer(1, 0, 0, Color.BLACK);

        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, 0xFF000000); //black border with full opacity
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            label_view.setBackgroundDrawable(border);
        } else {
            label_view.setBackground(border);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 2f;
        label_view.setLayoutParams(params);
        //     label_view.setEllipsize(TextUtils.TruncateAt.END);

        layout.addView(label_view, 2);
        return label_view;
    }


    private void initViews() {

        for (int i = 0; i < bars_list.size(); i++) {
            LinearLayout parent_layout = (LinearLayout) getChildAt(i);
            View bar;
            TextView score = null;
            if (parent_layout == null) {
                parent_layout = new LinearLayout(context);
                parent_layout.setWeightSum(11.0f);
                parent_layout.setOrientation(LinearLayout.VERTICAL);
                //   parent_layout.setPadding(5, 5, 5, 5);
                parent_layout.setGravity(Gravity.BOTTOM);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
                //    layoutParams.setMargins(1, 10, 1, 1);
                parent_layout.setLayoutParams(layoutParams);
                /*GradientDrawable border = new GradientDrawable();
                border.setStroke(1, 0xFF000000); //black border with full opacity
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    parent_layout.setBackgroundDrawable(border);
                } else {
                    parent_layout.setBackground(border);
                }*/

                //   parent_layout.setBackgroundColor(colors[i]);
                addScoreView(parent_layout, String.valueOf(bars_list.get(i).getPoint()));
                addBarView(parent_layout, i);
                addLabelView(parent_layout, bars_list.get(i).getLabel());
                addView(parent_layout, i);

            }
            parent_layout.invalidate();
        }
        this.invalidate();

        int biggest_point = 0;
        for (int i = 0; i < bars_list.size(); i++) {
            int point = bars_list.get(i).getPoint();
            if (biggest_point < point) {
                biggest_point = point;
            }

        }
        for (int i = 0; i < bars_list.size(); i++) {
            LinearLayout parent_layout = (LinearLayout) getChildAt(i);

            View bar = parent_layout.getChildAt(1);
            TextView score = (TextView) parent_layout.getChildAt(0);
            score.setText(bars_list.get(i).getPoint() + "");
            float weight = ((8 * ((float) bars_list.get(i).getPoint())) / biggest_point);
            LayoutParams view_layoutparams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
            view_layoutparams.weight = weight;
            bar.setLayoutParams(view_layoutparams);
            parent_layout.invalidate();
        }
    }


    public void setBarData(ArrayList<BarData> data_list, double height) {
        this.bars_list = data_list;
        //   this.setBackgroundColor(colors[9]);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, (int) height);
        this.setLayoutParams(layoutParams);
        this.invalidate();
        initViews();
    }
}
