package com.appdroidapps.mathster;

import android.view.View;

/**
 * Created by PanKaj on 1/10/2016.
 */
public class BarData {
    private  String label;
private  int point;
    public BarData(String label, int point) {
        this.label = label;
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public String getLabel() {
        return label;
    }
}
