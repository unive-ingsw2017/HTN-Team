package com.greenteadev.unive.clair.ui.history;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.common.collect.TreeMultimap;
import com.google.gson.Gson;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.reference.Bundles;
import com.greenteadev.unive.clair.reference.DateFormat;
import com.greenteadev.unive.clair.util.DateUtils;
import com.greenteadev.unive.clair.util.GraphDataHelper;
import com.greenteadev.unive.clair.util.chart.MonthXAxisFormatter;
import com.greenteadev.unive.clair.util.chart.WeekXAxisFormatter;
import com.greenteadev.unive.clair.util.chart.YearXAxisFormatter;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.greenteadev.unive.clair.R;
import timber.log.Timber;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class MeasureFragment extends Fragment {

    @BindView(R.id.chart)
    LineChart mChart;

    @BindView(R.id.tvBottomDescription)
    AppCompatTextView mBottomDescription;

    @BindView(R.id.tvTrend)
    AppCompatTextView mBottomTrend;

    @BindView(R.id.iwTrend)
    AppCompatImageView mImageTrend;

    @BindColor(R.color.theme_primary)
    int mPrimaryColor;

    @BindColor(R.color.theme_primary_dark)
    int mPrimaryDarkColor;

    @BindColor(R.color.theme_palette_orange)
    int mPaletteOrangeColor;

    @BindColor(R.color.theme_palette_red)
    int mPaletteRedColor;

    @BindColor(R.color.theme_palette_yellow)
    int mPaletteYellowColor;

    @BindColor(R.color.theme_palette_green)
    int mPaletteGreenColor;

    @BindColor(R.color.theme_palette_violet)
    int mPaletteVioletColor;

    View myFragmentView;
    LayoutInflater mInflater;
    Unbinder unbinder;

    LineData mData;
    LineDataSet mDataSet;
    LineDataSet mDataSetFake;

    String mKey;
    String mKeyString;
    List<MeasurePlotData> mDataList;
    LocalDate mTodayDate;
    LocalDate mTargetDate;
    List<Integer> mLimitsValues;
    int mSelectedPeriod = -1;

    public MeasureFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MeasureFragment newInstance(String key, String keyValue, ArrayList<Integer> limits,
                                              ArrayList<MeasurePlotData> dataList,
                                              LocalDate today, LocalDate targetDate,
                                              int period) {
        MeasureFragment fragment = new MeasureFragment();

        Bundle args = new Bundle();
        args.putString(Bundles.DATA_TYPE, key);
        args.putString(Bundles.DATA_TYPE_STRING, keyValue);
        args.putIntegerArrayList(Bundles.DATA_LIMIT, limits);
        args.putParcelableArrayList(Bundles.STATION_DATA, dataList);
        args.putString(Bundles.DATE_TODAY, today.toString());
        args.putString(Bundles.DATE_VALUE, targetDate.toString());
        args.putInt(Bundles.DATE_PERIOD, period);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        myFragmentView = inflater.inflate(R.layout.fragment_history_data, container, false);
        unbinder = ButterKnife.bind(this, myFragmentView);

        Gson gson = new Gson();

        mKey = getArguments().getString(Bundles.DATA_TYPE);
        mKeyString = getArguments().getString(Bundles.DATA_TYPE_STRING);
        mLimitsValues = getArguments().getIntegerArrayList(Bundles.DATA_LIMIT);
        mDataList = getArguments().getParcelableArrayList(Bundles.STATION_DATA);
        mTodayDate = LocalDate.parse(getArguments().getString(Bundles.DATE_TODAY));
        mTargetDate = LocalDate.parse(getArguments().getString(Bundles.DATE_VALUE));
        mSelectedPeriod = getArguments().getInt(Bundles.DATE_PERIOD);

        Timber.d("%s: %s", Bundles.DATA_TYPE, mKey);
        Timber.d("%s: %s", Bundles.DATA_TYPE_STRING, mKeyString);
        Timber.d("%s: %s", Bundles.DATA_LIMIT, mLimitsValues);
        Timber.d("%s: %s", Bundles.STATION_DATA, mDataList);
        Timber.d("%s: %s", Bundles.DATE_TODAY, mTodayDate);
        Timber.d("%s: %s", Bundles.DATE_VALUE, mTargetDate);
        Timber.d("%s: %s", Bundles.DATE_PERIOD, mSelectedPeriod);

        preInitChart();

        return myFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            processGraph();
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    public void updateControl(int period, LocalDate targetDate) {
        mSelectedPeriod = period;
        mTargetDate = targetDate;

        if (getUserVisibleHint()) {
            processGraph();
        }
    }


    private void preInitChart() {
        // Remove background and disable touch
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(false);
        mChart.setDescription(null);
        mChart.getLegend().setEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setHardwareAccelerationEnabled(true);

        // Disable axes
        //mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisLeft().removeAllLimitLines();
        //mChart.getAxisLeft().setDrawZeroLine(false);
        mChart.getAxisLeft().setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setTextSize(10f);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setAvoidFirstLastClipping(true);

        // Animations
        //mChart.animateXY(3000, 3000, Easing.EasingOption.Linear, Easing.EasingOption.EaseInBack);

        mData = new LineData();

        List<Entry> mLimitEntries = new ArrayList<>();
        int x = 0;
        for (Integer limitD : mLimitsValues) {
            Float limit = Float.valueOf(limitD);
            LimitLine line = new LimitLine(limit);

            line.setTextSize(12f);
            line.setTextColor(mPrimaryColor);
            line.setLabel(String.format("%.0f", limit));

            line.setLineWidth(2f);
            line.enableDashedLine(15f, 10f, 0f);
            line.setLineColor(mPrimaryColor);
            line.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

            mLimitEntries.add(new Entry(x, limit));
            mChart.getAxisLeft().addLimitLine(line);
            x++;
        }

        mDataSetFake = new LineDataSet(mLimitEntries, "Fake");
        //mDataSetFake.setDrawValues(false);
        //mDataSetFake.setDrawCircles(false);
        //mDataSetFake.setDrawCircleHole(false);
        mDataSetFake.setVisible(false);

        mDataSet = new LineDataSet(null, mKeyString);

        mDataSet.setLineWidth(5f);
        mDataSet.setCircleRadius(7f);

        mDataSet.setColor(mPrimaryColor);
        mDataSet.setCircleColor(mPrimaryColor);
        mDataSet.setDrawValues(false);
        mDataSet.setHighLightColor(Color.rgb(255, 255, 255));

        mDataSet.setDrawCircleHole(false);
        mDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        mDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        mData.addDataSet(mDataSetFake);
        mChart.setData(mData);

        mChart.fitScreen();
        mChart.invalidate();
    }

    private void processGraph() {
        if (mChart != null) {

            // Filter the data
            if (mTargetDate.isAfter(mTodayDate)) {
                mTargetDate = mTodayDate;
            }

            Pair<LocalDate, LocalDate> interval;

            switch (mSelectedPeriod) {
                case DateFormat.DATE_DAYS_MONTH:
                    interval = DateUtils.getMonthStartEnd(mTargetDate);
                    break;
                case DateFormat.DATE_DAYS_YEAR:
                    interval = DateUtils.getYearStartEnd(mTargetDate);
                    break;
                case DateFormat.DATE_DAYS_WEEK:
                default:
                    interval = DateUtils.getWeekStartEnd(mTargetDate);
                    break;
            }

            GraphDataHelper dataHelper = new GraphDataHelper(
                    mDataList, interval.first, interval.second);
            MeasureData.MeasureType keyType = MeasureData.MeasureType.valueOf(mKey);

            Timber.d("keyType %s", keyType);
            Timber.d("Interval %s - %s", interval.first, interval.second);

            mData.removeDataSet(mDataSet);
            mData.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mDataSet.clear();

            int x = 0;
            List<LocalDate> xAxeValues = dataHelper.getXAxeValues();
            for (LocalDate date : xAxeValues) {
                Timber.d("XVal %s", date);

                List<MeasurePlotData> list = dataHelper.getRawData(date);
                Timber.d("YVals %s", list);

                for (MeasurePlotData measure : list) {
                    float yValue = measure.avg();
                    if (measure.min() == -1) {
                        yValue = measure.max();
                    }
                    mDataSet.addEntry(new Entry(x, yValue, measure));
                }
                x++;
            }

            if (!mData.contains(mDataSet) && mDataSet.getEntryCount() > 0) {
                mData.addDataSet(mDataSet);
                mData.notifyDataChanged();

                Timber.d("dataSet size %s", mDataSet.getEntryCount());
                mDataSet.setDrawCircles(mSelectedPeriod != DateFormat.DATE_DAYS_YEAR);

                float difference = dataHelper.getRawData(xAxeValues.get(0)).get(0).avg() -
                        dataHelper.getRawData(xAxeValues.get(xAxeValues.size() - 1)).get(0).avg();

                LocalDate bestDay = dataHelper.bestDay(keyType);

                mBottomDescription.setText(bestDay.toString(DateFormat.DATE_FORMAT_VIEW_FULL_NO_YEAR));
                mBottomTrend.setText(String.format("%.1f", Math.abs(difference)));

                if (difference > 0) {
                    mImageTrend.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                } else {
                    mImageTrend.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                }

                switch (mSelectedPeriod) {
                    case DateFormat.DATE_DAYS_MONTH:
                        mChart.setVisibleXRangeMaximum(DateFormat.DATE_DAYS_MONTH);
                        mChart.getXAxis().setValueFormatter(new MonthXAxisFormatter(xAxeValues, false));
                        mChart.getXAxis().setLabelCount(mDataSet.getEntryCount() / DateFormat.DATE_WEEKS_MONTH, true);
                        //mChart.setScaleX(mDataSet.getEntryCount() / ((float) DateFormat.DATE_DAYS_MONTH));
                        break;
                    case DateFormat.DATE_DAYS_YEAR:
                        mChart.setVisibleXRangeMaximum(DateFormat.DATE_DAYS_YEAR);
                        mChart.getXAxis().setValueFormatter(new YearXAxisFormatter(xAxeValues, false));
                        mChart.getXAxis().setLabelCount(mDataSet.getEntryCount() / DateFormat.DATE_DAYS_MONTH, true);
                        //mChart.setScaleX(mDataSet.getEntryCount() / ((float) DateFormat.DATE_DAYS_YEAR));
                        break;
                    case DateFormat.DATE_DAYS_WEEK:
                    default:
                        mChart.setVisibleXRangeMaximum(DateFormat.DATE_DAYS_WEEK);
                        mChart.getXAxis().setValueFormatter(new WeekXAxisFormatter(xAxeValues, false));
                        mChart.getXAxis().setLabelCount(mDataSet.getEntryCount(), true);
                        //mChart.setScaleX(mDataSet.getEntryCount() / ((float) DateFormat.DATE_DAYS_WEEK));
                        break;
                }
            }

            mChart.fitScreen();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }
}