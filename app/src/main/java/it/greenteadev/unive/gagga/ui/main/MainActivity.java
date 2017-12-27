package it.greenteadev.unive.gagga.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.leakcanary.RefWatcher;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.greenteadev.unive.gagga.R;
import it.greenteadev.unive.gagga.data.model.Coordinate;
import it.greenteadev.unive.gagga.data.model.Station;
import it.greenteadev.unive.gagga.lib.BottomSheetAnchorBehavior;
import it.greenteadev.unive.gagga.reference.Map;
import it.greenteadev.unive.gagga.ui.base.BaseActivity;
import it.greenteadev.unive.gagga.ui.widget.ProgressFrameLayout;
import it.greenteadev.unive.gagga.util.DialogFactory;
import it.greenteadev.unive.gagga.util.PermissionUtils;
import it.greenteadev.unive.gagga.util.ViewUtil;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainMvpView, OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLoadedCallback, GoogleMap.OnMapClickListener, FloatingSearchView.OnSearchListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "uk.co.ribot.androidboilerplate.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    MainPresenter mMainPresenter;

    @Inject
    RefWatcher mRefWatcher;

    @BindView(R.id.main_content)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.parallax)
    RelativeLayout mParallaxLayout;

    @BindView(R.id.bottom_sheet)
    NestedScrollView mBottomSheetLayout;

    @BindView(R.id.toolbar)
    FloatingSearchView mToolbar;

    @BindView(R.id.progress)
    ProgressFrameLayout progressRelativeLayout;

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

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    @State
    boolean mPermissionDenied = false;

    @State
    ArrayList<Station> mDataLoaded = null;

    @State
    String mSelectedMarker = null;

    HashMap<String, Marker> mMarkerData = new HashMap<>();

    FragmentManager mFragmentManager;

    GoogleMap mGoogleMap;
    LatLngBounds.Builder mLatLngBuilder;

    Drawable emptyDrawable;
    Drawable errorConnectionDrawable;

    BottomSheetAnchorBehavior mBehaviour;

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

        if (mDataLoaded == null)
            progressRelativeLayout.showLoading();

        mToolbar.setOnSearchListener(this);

        mFragmentManager = getSupportFragmentManager();

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        emptyDrawable = new IconDrawable(this, MaterialIcons.md_explore)
                .colorRes(android.R.color.white);

        errorConnectionDrawable = new IconDrawable(this, MaterialIcons.md_cloud_off)
                .colorRes(android.R.color.white);

        /**
         * If we want to listen for states callback
         */
        mBehaviour = BottomSheetAnchorBehavior.from(mBottomSheetLayout);
        mBehaviour.setState(BottomSheetAnchorBehavior.STATE_HIDDEN);
        mBehaviour.setParallax(mParallaxLayout);

        // wait for the bottomsheet to be laid out
        mBottomSheetLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // set the height of the parallax to fill the gap between the anchor and the top of the screen
                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(mParallaxLayout.getMeasuredWidth(), mBehaviour.getAnchorOffset() / 2);
                mParallaxLayout.setLayoutParams(layoutParams);
                mBottomSheetLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
        mRefWatcher.watch(mMarkerData);
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
        mBehaviour.anchorMap(mGoogleMap);
        mBehaviour.setMapPadding(ViewUtil.dpToPx(mMapPaddingLeft),
                ViewUtil.dpToPx(mMapPaddingTop),
                ViewUtil.dpToPx(mMapPaddingRight),
                ViewUtil.dpToPx(mMapPaddingBottom));

        mGoogleMap.setPadding(
                ViewUtil.dpToPx(mMapPaddingLeft),
                ViewUtil.dpToPx(mMapPaddingTop),
                ViewUtil.dpToPx(mMapPaddingRight),
                ViewUtil.dpToPx(mMapPaddingBottom)
        );

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        //mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        //mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.setMinZoomPreference(7.8f);

        // Constrain the camera target to the Veneto bounds.
        mGoogleMap.setLatLngBoundsForCameraTarget(Map.Bound.VENETO);
        mGoogleMap.setOnMapLoadedCallback(this);

        enableMyLocation();

        // If no current data is present load them from DataManager
        // else use the data from bundle instance
        if (mDataLoaded == null)
            mMainPresenter.loadStations();
        else
            loadMarkersOnMap(mDataLoaded);
    }

    /**
     * Manipulates the camera when it's available.
     * The API invokes this callback when the map is rendered.
     */
    @Override
    public void onMapLoaded() {
        if (mLatLngBuilder != null && mSelectedMarker == null) {
            globalZoomMap();
        } else if (mSelectedMarker != null) {
            emulateMarkerClickMap(mMarkerData.get(mSelectedMarker));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mSelectedMarker = (String) marker.getTag();
        mMainPresenter.loadStationById(mSelectedMarker);
        //mToolbar.showProgress();
        showLoadingBottomSheet();
        markerZoomMap(marker);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mToolbar.clearQuery();
        mMainPresenter.disposeLoadStation();
        mSelectedMarker = null;
        globalZoomMap();
        hideBottomSheet();
    }

    /***** Floating Toolbar callback implementation *****/

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        if (searchSuggestion instanceof Station) {
            Station station = (Station) searchSuggestion;
            Marker marker = mMarkerData.get(station.codseqst());
            // Emulate user click
            emulateMarkerClickMap(marker);
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
    private void loadMarkersOnMap(List<Station> stationList) {
        mLatLngBuilder = new LatLngBounds.Builder();

        for (Station s : stationList) {
            try {
                Coordinate c = s.coordinate();
                LatLng location = new LatLng(Double.parseDouble(c.lat()), Double.parseDouble(c.lon()));
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(location).title(s.nome()));
                marker.setTag(s.codseqst());
                mMarkerData.put(s.codseqst(), marker);
                mLatLngBuilder.include(location);

                if (s.codseqst().equals(mSelectedMarker)) {
                    // Emulate user click
                    emulateMarkerClickMap(marker);
                }
            } catch (Exception e) {
                DialogFactory.createGenericErrorDialog(this,
                        getString(R.string.error_loading_stations))
                        .show();
            }
        }
    }

    /**
     * Perform a global Zoom on the map
     */
    private void globalZoomMap() {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mLatLngBuilder.build(), ViewUtil.dpToPx(mHorizontalMargin)));
    }

    /**
     * Perform a Zoom on a marker in the map
     */
    private void markerZoomMap(Marker marker) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
    }

    /**
     * Perform a Zoom on a marker in the map
     */
    private void emulateMarkerClickMap(Marker marker) {
        //marker.showInfoWindow();
        onMarkerClick(marker);
    }

    /**
     * Displays a loading bottom sheet
     */
    private void showLoadingBottomSheet() {
        LinearLayout header = mBottomSheetLayout.findViewById(R.id.bottom_sheet_header);
        AVLoadingIndicatorView loader = mBottomSheetLayout.findViewById(R.id.bottom_sheet_loader);

        mBehaviour.setLocked(true);
        mBehaviour.setHideable(false);
        mBehaviour.setState(BottomSheetAnchorBehavior.STATE_COLLAPSED);

        header.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }

    /**
     * Displays a loading bottom sheet
     */
    private void showDataBottomSheet(Station station) {
        LinearLayout header = mBottomSheetLayout.findViewById(R.id.bottom_sheet_header);
        AVLoadingIndicatorView loader = mBottomSheetLayout.findViewById(R.id.bottom_sheet_loader);

        AppCompatTextView title = header.findViewById(R.id.bottom_sheet_title);
        AppCompatTextView description = header.findViewById(R.id.bottom_sheet_description);

        title.setText(station.nome());
        description.setText(String.format("%s - %s,%s",
                station.localita(), station.comune(), station.provincia()));

        mBehaviour.setLocked(false);
        mBehaviour.setHideable(false);
        mBehaviour.setState(BottomSheetAnchorBehavior.STATE_COLLAPSED);

        header.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
        //mToolbar.hideProgress();
    }

    /**
     * Displays a loading bottom sheet
     */
    private void hideBottomSheet() {
        mBehaviour.setLocked(false);
        mBehaviour.setHideable(true);
        mBehaviour.setState(BottomSheetAnchorBehavior.STATE_HIDDEN);
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showStations(List<Station> stations) {
        loadMarkersOnMap(stations);

        progressRelativeLayout.showContent();
        mDataLoaded = new ArrayList<>(stations);
    }

    @Override
    public void showStationsEmpty() {
        progressRelativeLayout.showEmpty(emptyDrawable,
                getString(R.string.empty_data),
                getString(R.string.empty_data_description));
        mDataLoaded = new ArrayList<>();
    }

    @Override
    public void showStation(Station station) {
        mToolbar.setSearchText(station.nome());
        showDataBottomSheet(station);
    }

    @Override
    public void showStationEmpty() {
        Toast.makeText(this, R.string.empty_data, Toast.LENGTH_LONG).show();
        mBehaviour.setState(BottomSheetAnchorBehavior.STATE_HIDDEN);
    }

    @Override
    public void showStationsResult(List<Station> stations, boolean suggestion) {
        if (suggestion) {
            mToolbar.swapSuggestions(stations);
            //mToolbar.hideProgress();
        } else if (!stations.isEmpty()) {
            Marker marker = mMarkerData.get(stations.get(0).codseqst());
            // Emulate user click
            emulateMarkerClickMap(marker);
        }
    }

    @Override
    public void showError(boolean networkError) {
        if (networkError) {
            progressRelativeLayout.showError(errorConnectionDrawable,
                    getString(R.string.error_offline),
                    getString(R.string.error_offline_description),
                    getString(R.string.button_retry), view -> {
                        progressRelativeLayout.showLoading();
                        mMainPresenter.loadStations();
                    });
        } else {
            DialogFactory.createGenericErrorDialog(this,
                    getString(R.string.error_loading_generics))
                    .show();
        }
    }
}
