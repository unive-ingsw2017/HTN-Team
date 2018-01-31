package com.greenteadev.unive.clair.util.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.greenteadev.unive.clair.reference.DateFormat;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class MonthXAxisFormatter implements IAxisValueFormatter {

    List<LocalDate> mValues;
    boolean mForce = false;

    public MonthXAxisFormatter(List<LocalDate> values) {
        this(values, true);
    }

    public MonthXAxisFormatter(List<LocalDate> values, boolean force) {
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

        return mValues.get(myVal).toString(DateFormat.DATE_FORMAT_DAY_MONTH);
    }
}
