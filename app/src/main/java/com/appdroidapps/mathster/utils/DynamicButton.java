package com.appdroidapps.mathster.utils;

/**
 * Created by pranay on 12/01/16.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

import com.appdroidapps.mathster.MyApplication;

public class DynamicButton extends Button {

    DynamicButton that = this;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //     this.post(resize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      //  canvas.drawPaint(MyApplication.createCheckerBoard(5));
        this.setTypeface(MyApplication.mathsterFont);
    }

    private Runnable resize = new Runnable() {
        public void run() {
         /*   View v = (View) that.getParent();
            float f = that.getTextSize();
            Log.i(DynamicButton.class.getName(), that.getText() + " : " + f + " resized to " + that.getHeight());
            that.setTextSize(TypedValue.COMPLEX_UNIT_PX, 0.9f*that.getHeight());*/
        }
    };

    public DynamicButton(Context context) {
        super(context);
    }

    public DynamicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}