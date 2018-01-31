package com.greenteadev.unive.clair.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.collect.TreeMultimap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.data.model.StationMeasure;
import com.greenteadev.unive.clair.reference.Bundles;
import com.greenteadev.unive.clair.reference.DateFormat;
import com.greenteadev.unive.clair.reference.MeasureThreshold;
import com.greenteadev.unive.clair.ui.about.AboutActivity;
import com.greenteadev.unive.clair.ui.base.BaseActivity;
import com.greenteadev.unive.clair.ui.history.HistoryActivity;
import com.greenteadev.unive.clair.ui.settings.SettingsActivity;
import com.greenteadev.unive.clair.ui.widget.ProgressFrameLayout;
import com.greenteadev.unive.clair.ui.widget.bottomsheet.WABottomSheetBehavior;
import com.greenteadev.unive.clair.util.DateUtils;
import com.greenteadev.unive.clair.util.DialogFactory;
import com.greenteadev.unive.clair.util.GraphDataHelper;
import com.greenteadev.unive.clair.util.PermissionUtils;
import com.greenteadev.unive.clair.util.ViewUtil;
import com.greenteadev.unive.clair.util.chart.WeekXAxisFormatter;
import com.greenteadev.unive.clair.util.ui.StationClusterRenderer;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.leakcanary.RefWatcher;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.greenteadev.unive.clair.R;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMapLongClickListener, ClusterManager.OnClusterClickListener<StationMeasure>,
        ClusterManager.OnClusterItemClickListener<StationMeasure>,
        FloatingSearchView.OnSearchListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "com.greenteadev.unive.clair.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    public enum Status {
        DEFAULT, LOADING, ERROR, DONE, LOADING_STATION_DATA, ERROR_STATION_DATA, DONE_STATION_DATA
    }

    @Inject
    MainPresenter mMainPresenter;

    @Inject
    RefWatcher mRefWatcher;

    @BindView(R.id.main_content)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.bottom_sheet)
    NestedScrollView mBottomSheetLayout;

    @BindView(R.id.progress)
    ProgressFrameLayout progressLayout;

    @BindView(R.id.toolbar)
    FloatingSearchView mToolbar;

    @BindView(R.id.btn_location)
    FloatingActionButton mFloatingButton;

    @BindView(R.id.bottom_sheet_header)
    RelativeLayout mBottomSheetHeader;

    @BindView(R.id.bottom_sheet_loader)
    AVLoadingIndicatorView mBottomSheetLoader;

    @BindView(R.id.bottom_sheet_content)
    LinearLayout mBottomSheetContent;

    @BindView(R.id.button_bookmark)
    SparkButton mBookmarkButton;

    @BindView(R.id.btn_calendar)
    AppCompatImageButton mHistoryButton;

    @BindView(R.id.week_chart)
    LineChart mGraph;

    @BindView(R.id.tvPMUnit)
    AppCompatTextView mPMUnit;

    @BindView(R.id.tvOzoneUnit)
    AppCompatTextView mOzoneUnit;


    @BindDimen(R.dimen.vertical_margin)
    int mVerticalMargin;

    @BindDimen(R.dimen.horizontal_margin)
    int mHorizontalMargin;

    @BindDimen(R.dimen.maps_padding_left)
    int mMapPaddingLeft;

    @BindDimen(R.dimen.maps_padding_top)
    int mMapPaddingTop;

    @BindDimen(R.dimen.maps_padding_right)
    int mMapPaddingRight;

    @BindDimen(R.dimen.maps_padding_bottom)
    int mMapPaddingBottom;

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

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    @State
    boolean mPermissionDenied = false;

    @State
    Status mActivityState = Status.DEFAULT;

    @State
    ArrayList<StationMeasure> mDataLoaded = null;

    @State
    Station mSelectedStation = null;

    FragmentManager mFragmentManager;

    GoogleMap mGoogleMap;
    LatLngBounds.Builder mLatLngBuilder;
    ClusterManager<StationMeasure> mClusterManager;

    Drawable emptyDrawable;
    Drawable errorConnectionDrawable;

    WABottomSheetBehavior mBehaviour;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter.attachView(this);
        mMainPresenter.attachToolbarView(mToolbar);

        if (mActivityState == Status.DEFAULT || mActivityState == Status.ERROR) {
            mActivityState = Status.LOADING;
            progressLayout.showLoading();
        }

        mToolbar.setOnSearchListener(this);
        mToolbar.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    //case R.id.action_reload:
                    //    break;
                    case R.id.action_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.action_about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });

        mFragmentManager = getSupportFragmentManager();

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        emptyDrawable = new IconDrawable(this, MaterialIcons.md_explore)
                .colorRes(android.R.color.white);

        errorConnectionDrawable = new IconDrawable(this, MaterialIcons.md_cloud_off)
                .colorRes(android.R.color.white);


        /**
         * Ee want to listen for states callback
         */
        mBehaviour = WABottomSheetBehavior.from(mBottomSheetLayout);

        mBehaviour.setState(WABottomSheetBehavior.STATE_HIDDEN);
        mBehaviour.setBottomSheetCallback(new WABottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @WABottomSheetBehavior.State int newState) {

                //if (mActivityState == Status.LOADING_STATION_DATA) {
                //    mBehaviour.setState(WABottomSheetBehavior.STATE_COLLAPSED);
                //} else {
                if (newState == WABottomSheetBehavior.STATE_EXPANDED) {
                    mBookmarkButton.setVisibility(View.VISIBLE);
                    if (mBookmarkButton.isChecked())
                        mBookmarkButton.playAnimation();
                } else {
                    mBookmarkButton.setVisibility(View.INVISIBLE);
                }
                //}
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        // Setup chart
        mGraph.setAutoScaleMinMaxEnabled(true);
        mGraph.setPinchZoom(false);
        mGraph.setDoubleTapToZoomEnabled(false);
        mGraph.setDescription(null);
        mGraph.setHardwareAccelerationEnabled(true);

        Legend legend = mGraph.getLegend();
        legend.setDrawInside(true);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextColor(mPrimaryDarkColor);

        XAxis xAxis = mGraph.getXAxis();
        YAxis leftAxis = mGraph.getAxisLeft();
        YAxis rightAxis = mGraph.getAxisRight();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelRotationAngle(45);
        xAxis.setTextColor(mPrimaryDarkColor);
        xAxis.setAxisLineColor(mPrimaryDarkColor);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setAvoidFirstLastClipping(true);

        leftAxis.setTextColor(mPrimaryDarkColor);
        leftAxis.setGridColor(mPrimaryDarkColor);
        leftAxis.setAxisLineColor(mPrimaryDarkColor);

        rightAxis.setTextColor(mPrimaryDarkColor);
        rightAxis.setGridColor(mPrimaryDarkColor);
        rightAxis.setAxisLineColor(mPrimaryDarkColor);

        mBookmarkButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (mActivityState == Status.DONE_STATION_DATA && mSelectedStation != null) {
                    mMainPresenter.bookmarkStation(mSelectedStation.id(), buttonState);
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView imageView, boolean b) {
                // Nothing here.
            }

            @Override
            public void onEventAnimationStart(ImageView imageView, boolean b) {
                // Nothing here.
            }
        });

        mPMUnit.setText(Html.fromHtml(getString(R.string.ug_m3)), TextView.BufferType.SPANNABLE);
        mOzoneUnit.setText(Html.fromHtml(getString(R.string.ug_m3)), TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.attachView(this);
        mMainPresenter.attachToolbarView(mToolbar);

        //Force to remove bug
        //mToolbar.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_NO_LEFT_ACTION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.detachView();
        mMainPresenter.detachToolbarView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        if (mActivityState == Status.LOADING_STATION_DATA || mActivityState == Status.DONE_STATION_DATA) {
            globalZoomMap();
            hideBottomSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        StateSaver.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @OnClick(R.id.btn_location)
    public void onFABClick() {
        if (mGoogleMap.isMyLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
                return;
            }

            Location location = mGoogleMap.getMyLocation();
            if (location != null)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        location.getLatitude(), location.getLongitude()), 15));
        } else
            showMissingPermissionError();
    }

    @OnClick(R.id.btn_calendar)
    public void onHistoryClick() {
        if (mActivityState == Status.DONE_STATION_DATA && mSelectedStation != null) {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra(Bundles.STATION, mSelectedStation);
            startActivity(intent);
        }
    }

    /***** GMaps callback implementation *****/

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mGoogleMap = googleMap;
        /*mBehaviour.(mGoogleMap);
        mBehaviour.setMapPadding(
                ViewUtil.dpToPx(mMapPaddingLeft),
                ViewUtil.dpToPx(mMapPaddingTop),
                ViewUtil.dpToPx(mMapPaddingRight),
                ViewUtil.dpToPx(mMapPaddingBottom));*/

        mGoogleMap.setPadding(
                ViewUtil.dpToPx(mMapPaddingLeft),
                ViewUtil.dpToPx(mMapPaddingTop),
                ViewUtil.dpToPx(mMapPaddingRight),
                ViewUtil.dpToPx(mMapPaddingBottom)
        );

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        //mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.setMinZoomPreference(7.8f);

        // Constrain the camera target to the Veneto bounds.
        //mGoogleMap.setLatLngBoundsForCameraTarget(Map.Bound.VENETO);

        mGoogleMap.setOnMapLoadedCallback(this);
        mGoogleMap.setOnMapLongClickListener(this);

        enableMyLocation();
    }

    /**
     * Manipulates the camera when it's available.
     * The API invokes this callback when the map is rendered.
     */
    @Override
    public void onMapLoaded() {
        Timber.d("onMapLoaded()");
        // Setup ClusterManager
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<StationMeasure>(this, mGoogleMap);
            mClusterManager.setRenderer(new StationClusterRenderer(this,mGoogleMap, mClusterManager));
            mClusterManager.setOnClusterItemClickListener(this);
            mClusterManager.setOnClusterClickListener(this);

            mGoogleMap.setOnMarkerClickListener(mClusterManager);
            mGoogleMap.setOnMarkerClickListener(mClusterManager);
            mGoogleMap.setOnCameraIdleListener(mClusterManager);
            mGoogleMap.setOnInfoWindowClickListener(mClusterManager);
        }

        // If no current data is present load them from DataManager
        // else use the data from bundle instance
        if (mActivityState == Status.LOADING)
            mMainPresenter.loadStations();
        else
            loadMarkersOnMap(mDataLoaded);
    }

    @Override
    public boolean onClusterClick(Cluster<StationMeasure> cluster) {
        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }

        // Animate camera to the bounds
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    builder.build(), ViewUtil.dpToPx(mHorizontalMargin)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onClusterItemClick(StationMeasure station) {
        mSelectedStation = station.station();
        markerZoomMap(station.getPosition(), true);
        showLoadingBottomSheet();
        mMainPresenter.loadStationDataById(mSelectedStation.id());
        mMainPresenter.isBookmarkStation(mSelectedStation.id());

        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mToolbar.clearQuery();
        mMainPresenter.disposeLoadStation();
        globalZoomMap();
        hideBottomSheet();
    }

    /***** Floating Toolbar callback implementation *****/

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        if (searchSuggestion instanceof Station) {
            Station station = (Station) searchSuggestion;
            // Emulate click
            emulateMarkerClickMap(station, true);
        }
    }

    @Override
    public void onSearchAction(String currentQuery) {
        mMainPresenter.searchStationByName(currentQuery, false);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Load the Markers to the map
     */
    private void loadMarkersOnMap(List<StationMeasure> stationList) {
        mLatLngBuilder = new LatLngBounds.Builder();

        mClusterManager.addItems(stationList);
        for (StationMeasure s : stationList) {
            mLatLngBuilder.include(s.station().getPosition());
        }

        // Constrain the camera target to the Marker bounds.
        mGoogleMap.setLatLngBoundsForCameraTarget(mLatLngBuilder.build());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                mLatLngBuilder.build(), ViewUtil.dpToPx(mHorizontalMargin)));

        if (mActivityState == Status.LOADING) {
            progressLayout.showContent();
            mActivityState = Status.DONE;
        }

        // If there was a selected station mark it!
        if (mActivityState == Status.LOADING_STATION_DATA ||
                mActivityState == Status.DONE_STATION_DATA) {
            // Emulate user click
            emulateMarkerClickMap(mSelectedStation, false);
        }
    }

    /**
     * Perform a global Zoom on the map
     */
    private void globalZoomMap() {
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    mLatLngBuilder.build(), ViewUtil.dpToPx(mHorizontalMargin)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform a Zoom on a marker in the map
     */
    private void markerZoomMap(LatLng position, boolean animate) {
        try {
            if (animate)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
            else
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform a Zoom on a marker in the map
     */
    private void emulateMarkerClickMap(Station station, boolean byUser) {
        markerZoomMap(station.getPosition(), byUser);

        showLoadingBottomSheet();
        mMainPresenter.loadStationDataById(station.id());
        mMainPresenter.isBookmarkStation(station.id());
    }

    /**
     * Displays a loading bottom sheet
     */
    private void showLoadingBottomSheet() {
        mActivityState = Status.LOADING_STATION_DATA;

        mBehaviour.setLocked(true);
        mBehaviour.setHideable(false);
        mBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetHeader.setVisibility(View.GONE);
        mBottomSheetLoader.setVisibility(View.VISIBLE);
    }

    /**
     * Displays data in bottom sheet
     */
    private void showDataBottomSheet(Station station, List<MeasurePlotData> measures) {
        mActivityState = Status.DONE_STATION_DATA;

        if (!(measures.size() > 0)) {
            showError(false);
            return;
        }

        AppCompatTextView title = mBottomSheetHeader.findViewById(R.id.bottom_sheet_title);
        AppCompatTextView description = mBottomSheetHeader.findViewById(R.id.bottom_sheet_description);

        AppCompatTextView ozoneValue = mBottomSheetContent.findViewById(R.id.tvOzoneValue);
        AppCompatTextView pm10Value = mBottomSheetContent.findViewById(R.id.tvPMValue);

        AppCompatTextView ozoneDate = mBottomSheetContent.findViewById(R.id.tvOzoneDate);
        AppCompatTextView pm10Date = mBottomSheetContent.findViewById(R.id.tvPMDate);

        title.setText(station.nome());
        description.setText(String.format("%s - %s,%s",
                station.localita(), station.comune(), station.provincia()));

        mBehaviour.setLocked(false);
        mBehaviour.setHideable(false);
        mBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetHeader.setVisibility(View.VISIBLE);
        mBottomSheetLoader.setVisibility(View.GONE);
        mToolbar.setSearchText(station.nome());
        mToolbar.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_NO_LEFT_ACTION);

        mGraph.clear();
        mGraph.invalidate();

        int colorPalette[] = {mPaletteRedColor, mPaletteOrangeColor, mPaletteGreenColor,
                mPaletteYellowColor, mPaletteVioletColor};

        LocalDate today = new LocalDate();
        LocalDate lastWeek = DateUtils.getLast7Day(today);
        GraphDataHelper dataHelper = new GraphDataHelper(measures, lastWeek, today);

        HashMap<MeasureData.MeasureType, LineDataSet> dataSets = new HashMap<>();

        int x = 0;
        int setCount = 0;
        for (LocalDate date : dataHelper.getXAxeValues()) {
            TreeMultimap<MeasureData.MeasureType, MeasurePlotData> map = dataHelper.getDataSet(date);

            for (MeasureData.MeasureType key : map.keySet()) {
                LineDataSet iLineDataSet = dataSets.get(key);

                if (iLineDataSet == null) {
                    iLineDataSet = new LineDataSet(new ArrayList<>(),
                            getStringResourceByName(key.toString().toLowerCase())); // add entries to dataset
                    int lineColor = colorPalette[setCount % colorPalette.length];

                    iLineDataSet.setColor(lineColor);
                    iLineDataSet.setValueTextColor(lineColor);
                    iLineDataSet.setCircleColorHole(getResources().getColor(R.color.white));
                    iLineDataSet.setCircleColor(lineColor);
                    iLineDataSet.setLineWidth(3f);
                    iLineDataSet.setCircleHoleRadius(3f);
                    iLineDataSet.setCircleRadius(6f);
                    iLineDataSet.setDrawValues(false);

                    iLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSets.put(key, iLineDataSet);
                    setCount++;
                }

                for (MeasurePlotData measure : map.get(key)) {
                    float yValue = measure.avg();
                    if (measure.min() == -1) {
                        yValue = measure.max();
                    }
                    iLineDataSet.addEntry(new Entry(x, yValue, measure));
                }
            }
            x++;
        }

        List<ILineDataSet> finalSets = new ArrayList<>();
        for (LineDataSet set : dataSets.values()) {
            // If Dataset have values than add them to the dataset
            if (set.getEntryCount() > 0)
                finalSets.add(set);
        }

        LineData data = new LineData(finalSets);
        data.setHighlightEnabled(false);

        mGraph.getXAxis().setValueFormatter(new WeekXAxisFormatter(dataHelper.getXAxeValues()));
        mGraph.setData(data);
        mGraph.fitScreen();
        mGraph.invalidate();

        if (dataSets.containsKey(MeasureData.MeasureType.OZONE)) {
            LineDataSet lineDataSet = dataSets.get(MeasureData.MeasureType.OZONE);
            int size = lineDataSet.getEntryCount();
            Entry entry = lineDataSet.getEntryForIndex(size - 1);
            float value = entry.getY();
            int color = mPaletteGreenColor;

            if (value >= MeasureThreshold.Ozone.MIN) {
                color = mPaletteYellowColor;
            }

            if (value >= MeasureThreshold.Ozone.MED) {
                color = mPaletteOrangeColor;
            }

            if (value >= MeasureThreshold.Ozone.MAX) {
                color = mPaletteRedColor;
            }

            ozoneValue.setText(String.format("%.1f", entry.getY()));
            ozoneDate.setText(((MeasurePlotData) entry.getData()).date().toString(
                    DateFormat.DATE_FORMAT_VIEW));
            ozoneValue.setTextColor(color);
            //mOzoneUnit.setTextColor(color);
        } else {
            ozoneValue.setText(getString(R.string.main_not_available));
            ozoneValue.setTextColor(mPrimaryColor);
            //mOzoneUnit.setTextColor(mPrimaryColor);
        }

        if (dataSets.containsKey(MeasureData.MeasureType.PM10)) {
            LineDataSet lineDataSet = dataSets.get(MeasureData.MeasureType.PM10);
            int size = lineDataSet.getEntryCount();
            Entry entry = lineDataSet.getEntryForIndex(size - 1);
            float value = entry.getY();
            int color = mPaletteGreenColor;

            if (value >= MeasureThreshold.PM10.MIN) {
                color = mPaletteOrangeColor;
            }

            if (value >= MeasureThreshold.PM10.MAX) {
                color = mPaletteRedColor;
            }

            pm10Value.setText(String.format("%.1f", entry.getY()));
            pm10Date.setText(((MeasurePlotData) entry.getData()).date().toString(
                    DateFormat.DATE_FORMAT_VIEW));

            pm10Value.setTextColor(color);
            //mPMUnit.setTextColor(color);
        } else {
            pm10Value.setText(getString(R.string.main_not_available));
            pm10Value.setTextColor(mPrimaryColor);
            //mPMUnit.setTextColor(mPrimaryColor);
        }
    }

    /**
     * Displays a loading bottom sheet
     */

    private void hideBottomSheet() {
        mActivityState = Status.DONE;

        mBehaviour.setLocked(false);
        mBehaviour.setHideable(true);
        mBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

        mToolbar.clearQuery();
        mMainPresenter.disposeLoadStation();
    }

    /***** MVP View methods implementation
     * @param stations*****/

    @Override
    public void showStations(List<StationMeasure> stations) {
        mDataLoaded = new ArrayList<>(stations);
        loadMarkersOnMap(mDataLoaded);
    }

    @Override
    public void showStationsEmpty() {
        progressLayout.showEmpty(emptyDrawable,
                getString(R.string.empty_data),
                getString(R.string.empty_data_description));
        mDataLoaded = new ArrayList<>();

        mActivityState = Status.ERROR;
    }

    @Override
    public void showStationData(StationMeasure measure) {
        showDataBottomSheet(measure.station(), measure.measures());
    }

    @Override
    public void showStationDataEmpty() {
        Toast.makeText(this, R.string.empty_data, Toast.LENGTH_LONG).show();
        mBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void showSearchResult(List<Station> stations, boolean suggestion) {
        Timber.d("showSearchResult() focus %b", mToolbar.isSearchBarFocused());
        if (stations.size() > 0 && mToolbar.isSearchBarFocused()) {
            if (suggestion) {
                mToolbar.swapSuggestions(stations);
                mToolbar.hideProgress();
            } else {
                mToolbar.clearSuggestions();
                emulateMarkerClickMap(stations.get(0), true);
            }
        }
        //Force to remove bug
        //mToolbar.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_NO_LEFT_ACTION);
    }

    @Override
    public void showBookmark(Boolean result) {
        mBookmarkButton.setChecked(result);
    }

    @Override
    public void showError(boolean networkError) {
        if (networkError) {
            progressLayout.showError(errorConnectionDrawable,
                    getString(R.string.error_offline),
                    getString(R.string.error_offline_description),
                    getString(R.string.button_retry), view -> {
                        progressLayout.showLoading();
                        mMainPresenter.loadStations();
                    });
        } else {
            DialogFactory.createGenericErrorDialog(this,
                    getString(R.string.error_loading_generics))
                    .show();
        }

        mActivityState = Status.ERROR;
        hideBottomSheet();
    }


    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }
}
