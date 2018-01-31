package com.greenteadev.unive.clair.ui.history;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.greenteadev.unive.clair.R;
import timber.log.Timber;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
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
import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.reference.Bundles;
import com.greenteadev.unive.clair.reference.DateFormat;
import com.greenteadev.unive.clair.reference.MeasureThreshold;
import com.greenteadev.unive.clair.ui.base.BaseActivity;
import com.greenteadev.unive.clair.ui.widget.ProgressFrameLayout;
import com.greenteadev.unive.clair.util.DateUtils;
import com.greenteadev.unive.clair.util.GraphDataHelper;
import com.greenteadev.unive.clair.util.chart.MonthXAxisFormatter;
import com.greenteadev.unive.clair.util.chart.WeekXAxisFormatter;
import com.greenteadev.unive.clair.util.chart.YearXAxisFormatter;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class HistoryActivity extends BaseActivity implements HistoryMvpView {

    @Inject
    HistoryPresenter mHistoryPresenter;

    @BindView(R.id.main_content)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.toolbar)
    Toolbar mTtoolbar;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.tvDate)
    AppCompatTextView mDateText;

    @BindView(R.id.iwBack)
    AppCompatImageView mBackward;

    @BindView(R.id.iwFordward)
    AppCompatImageView mForward;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView mbottomNavigationView;

    @BindView(R.id.progress)
    ProgressFrameLayout progressLayout;

    SectionsPagerAdapter mSectionsPagerAdapter;

    @State
    Station mStation;

    @State
    LocalDate mToday;

    @State
    LocalDate mFirstDay;

    @State
    LocalDate mTargetDate;

    @State
    int mSelectedPeriod = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        mHistoryPresenter.attachView(this);

        setSupportActionBar(mTtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mbottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_week:
                            mSelectedPeriod = DateFormat.DATE_DAYS_WEEK;
                            break;
                        case R.id.navigation_month:
                            mSelectedPeriod = DateFormat.DATE_DAYS_MONTH;
                            break;
                        case R.id.navigation_year:
                            mSelectedPeriod = DateFormat.DATE_DAYS_YEAR;
                            break;
                    }
                    mTargetDate = new LocalDate();
                    updateFragments();
                    return true;
                });

        progressLayout.showLoading();

        Intent intent = getIntent();
        if (intent != null) {
            mStation = intent.getParcelableExtra(Bundles.STATION);

            getSupportActionBar().setTitle(mStation.nome());
            getSupportActionBar().setSubtitle(String.format("%s - %s,%s",
                    mStation.localita(), mStation.comune(), mStation.provincia()));

            mHistoryPresenter.loadAllStationData(mStation.id());
        } else
            showError(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy()");
        mHistoryPresenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iwBack)
    public void onTimeBackClick(View v) {
        if (mSelectedPeriod == DateFormat.DATE_DAYS_WEEK) {
            mTargetDate = mTargetDate.minusWeeks(1);
        } else if (mSelectedPeriod == DateFormat.DATE_DAYS_MONTH) {
            mTargetDate = mTargetDate.minusMonths(1);
        } else {
            mTargetDate = mTargetDate.minusYears(1);
        }
        updateFragments();
    }

    @OnClick(R.id.iwFordward)
    public void onTimeForwardClick(View v) {
        if (mSelectedPeriod == DateFormat.DATE_DAYS_WEEK) {
            mTargetDate = mTargetDate.plusWeeks(1);
        } else if (mSelectedPeriod == DateFormat.DATE_DAYS_MONTH) {
            mTargetDate = mTargetDate.plusMonths(1);
        } else {
            mTargetDate = mTargetDate.plusYears(1);
        }

        updateFragments();
    }

    private void updateFragments() {
        LocalDate today = new LocalDate();

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

        mDateText.setText(String.format("%s - %s",
                interval.first.toString(DateFormat.DATE_FORMAT_VIEW_FULL),
                interval.second.toString(DateFormat.DATE_FORMAT_VIEW_FULL)
        ));


        if (mTargetDate.isAfter(today) || mTargetDate.equals(today)) {
            mForward.setVisibility(View.INVISIBLE);
        } else {
            mForward.setVisibility(View.VISIBLE);
        }

        Thread.dumpStack();

        if (mFirstDay == null) {
            Timber.d("updateFragments() mFirstDay: %s", mFirstDay);
            mBackward.setVisibility(View.INVISIBLE);
        } else {
            if (interval.first.isBefore(mFirstDay) || interval.first.equals(mFirstDay)) {
                Timber.d("updateFragments() mFirstDay: %s", mFirstDay);
                Timber.d("updateFragments() start: %s", interval.first);
                mBackward.setVisibility(View.INVISIBLE);
            } else {
                mBackward.setVisibility(View.VISIBLE);
            }
        }

        mSectionsPagerAdapter.updateAll(mSelectedPeriod, mTargetDate);
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showStationData(TreeMultimap<MeasureData.MeasureType, MeasurePlotData> measureMap) {
        mToday = new LocalDate();

        for (MeasurePlotData measure : measureMap.values()) {
            if (mFirstDay == null) {
                mFirstDay = measure.date();
            }
            if (measure.date().isBefore(mFirstDay)) {
                mFirstDay = measure.date();
            }
        }
        Timber.d("mFirstDay: %s", mFirstDay);

        if (mSelectedPeriod == -1) {
            mbottomNavigationView.setSelectedItemId(R.id.navigation_week);
        }

        if (measureMap.keySet().size() > 0) {
            for (MeasureData.MeasureType key : measureMap.keySet()) {
                ArrayList<Integer> limits = new ArrayList<>();
                switch (key) {
                    case OZONE:
                        limits.add(MeasureThreshold.Ozone.MIN);
                        limits.add(MeasureThreshold.Ozone.MED);
                        limits.add(MeasureThreshold.Ozone.MAX);
                        break;
                    case PM10:
                        limits.add(MeasureThreshold.PM10.MIN);
                        limits.add(MeasureThreshold.PM10.MAX);
                        break;
                    case UNKNOWN:
                    default:
                        break;
                }

                MeasureFragment measureFragment = MeasureFragment.newInstance(
                        key.toString(),
                        getStringResourceByName(key.toString().toLowerCase()),
                        limits,
                        new ArrayList<>(measureMap.get(key)),
                        mToday,
                        mToday,
                        mSelectedPeriod
                );

                mSectionsPagerAdapter.add(measureFragment, getStringResourceByName(key.toString().toLowerCase()));
            }
            mSectionsPagerAdapter.notifyDataSetChanged();
            progressLayout.showContent();
            return;
        }
        showError(false);
    }

    @Override
    public void showError(boolean networkError) {
        progressLayout.showError(new IconDrawable(this, MaterialIcons.md_error_outline)
                        .colorRes(android.R.color.white),
                getString(R.string.error_title),
                getString(R.string.error_generic),
                getString(R.string.button_back), view -> {
                    onBackPressed();
                });
    }

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<MeasureFragment> mFragmentList;
        private final List<String> mFragmentTitleList;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList = new ArrayList<>();
            mFragmentTitleList = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void clear() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        public void add(MeasureFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void addAll(List<MeasureFragment> fragmentList, List<String> titleList) {
            mFragmentList.addAll(fragmentList);
            mFragmentTitleList.addAll(titleList);
        }

        public void updateAll(int period, LocalDate targetDate) {
            for (MeasureFragment fragment : mFragmentList) {
                fragment.updateControl(period, targetDate);
            }
        }
    }
}
