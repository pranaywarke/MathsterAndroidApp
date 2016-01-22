package com.appdroidapps.mathster.utils;

/**
 * Created by pranay on 12/01/16.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DynamicTextView extends TextView {

    DynamicTextView that = this;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        this.post(resize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
 //       this.post(resize);
    }

    private Runnable resize = new Runnable() {
        public void run() {
          /*  View v = (View) that.getParent();
            float f = that.getTextSize();
            Log.i(DynamicTextView.class.getName(), that.getText() +" : "+ f + " resized to " + v.getHeight());
            that.setTextSize(v.getHeight());*/
        }
    };

    public DynamicTextView(Context context) {
        super(context);
    }

    public DynamicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
 }

    public DynamicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
  }
}