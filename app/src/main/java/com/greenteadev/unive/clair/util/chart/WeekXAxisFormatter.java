package com.greenteadev.unive.clair.util.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.LocalDate;

import java.util.List;

import com.greenteadev.unive.clair.reference.DateFormat;

/**
 * Created by Hitech95 on 22/01/2018.
 */

public class WeekXAxisFormatter implements IAxisValueFormatter {

    List<LocalDate> mValues;

    boolean mForce = false;

    public WeekXAxisFormatter(List<LocalDate> values) {
        this(values, true);
    }

    public WeekXAxisFormatter(List<LocalDate> values, boolean force) {
        mValues = values;
        mForce = force;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (mForce) axis.setLabelCount(mValues.size(), true);
        int myVal = Math.round(value);
        if (myVal < 0) {
            myVal = 0;
        }

        if (myVal >= mValues.size()) {
            myVal = mValues.size() - 1;
        }

        return mValues.get(myVal).toString(DateFormat.DATE_FORMAT_DAY);
    }
}
