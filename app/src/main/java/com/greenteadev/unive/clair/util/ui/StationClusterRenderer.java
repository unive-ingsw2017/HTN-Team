package com.greenteadev.unive.clair.util.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.TreeMultimap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.data.model.StationMeasure;
import com.greenteadev.unive.clair.reference.MeasureThreshold;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.greenteadev.unive.clair.R;
import timber.log.Timber;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class StationClusterRenderer extends DefaultClusterRenderer<StationMeasure> {

    Context mContext;
    GoogleMap mMap;

    LayoutInflater mLayoutInflater;

    IconGenerator mIconGenerator;

    @BindView(R.id.clTop)
    ConstraintLayout mContainerTop;

    @BindView(R.id.clBottom)
    ConstraintLayout mContainerBottom;

    @BindView(R.id.tvOzoneValue)
    AppCompatTextView mOzoneValue;

    @BindView(R.id.tvOzoneUnit)
    AppCompatTextView mOzoneUnit;

    @BindView(R.id.tvPmValue)
    AppCompatTextView mPmValue;

    @BindView(R.id.tvPmUnit)
    AppCompatTextView mPMUnit;

    @BindColor(R.color.theme_palette_orange)
    int mPaletteOrangeColor;

    @BindColor(R.color.theme_palette_red)
    int mPaletteRedColor;

    @BindColor(R.color.theme_palette_yellow)
    int mPaletteYellowColor;

    @BindColor(R.color.theme_palette_green)
    int mPaletteGreenColor;

    public StationClusterRenderer(Context context, GoogleMap map,
                                  ClusterManager<StationMeasure> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mMap = map;

        mIconGenerator = new IconGenerator(mContext);

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout rootView = (ConstraintLayout) mLayoutInflater.inflate(R.layout.marker_station, null);

        ButterKnife.bind(this, rootView);

        int mDimension = (int) mContext.getResources().getDimension(R.dimen.maps_marker_size);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));

        mIconGenerator.setContentView(rootView);
        /*mOzoneValue = rootView.findViewById(R.id.tvOzoneValue);
        mOzoneUnit = rootView.findViewById(R.id.tvOzoneUnit);
        mPmValue = rootView.findViewById(R.id.tvPmValue);
        mPMUnit = rootView.findViewById(R.id.tvPmUnit);*/

        mPMUnit.setText(Html.fromHtml(context.getString(R.string.ug_m3)),
                TextView.BufferType.SPANNABLE);
        mOzoneUnit.setText(Html.fromHtml(context.getString(R.string.ug_m3)),
                TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onBeforeClusterItemRendered(StationMeasure station, MarkerOptions markerOptions) {
        Timber.d("onBeforeClusterItemRendered()");

        Bitmap icon = generateBitmap(station.measures());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<StationMeasure> cluster, MarkerOptions markerOptions) {
        Timber.d("onBeforeClusterRendered()");
        List<MeasurePlotData> measuresMeasurePlotData = new ArrayList<>();

        for (StationMeasure stationMeasure : cluster.getItems()) {
            measuresMeasurePlotData.addAll(stationMeasure.measures());
        }

        Bitmap icon = generateBitmap(measuresMeasurePlotData);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }

    private Bitmap generateBitmap(List<MeasurePlotData> measures) {
        TreeMultimap<MeasureData.MeasureType, Float> averageMap = TreeMultimap.create();

        for (MeasurePlotData measurePlotData : measures) {
            averageMap.put(measurePlotData.type(), measurePlotData.avg());
        }

        for (MeasureData.MeasureType key : averageMap.keySet()) {
            float sum = 0;
            for (float val : averageMap.get(key)) {
                sum += val;
            }

            float value = sum / averageMap.get(key).size();
            String valueString = String.format("%.1f", value);
            int color = mPaletteGreenColor;
            switch (key) {
                case OZONE:

                    if (value >= MeasureThreshold.Ozone.MIN) {
                        color = mPaletteYellowColor;
                    }

                    if (value >= MeasureThreshold.Ozone.MED) {
                        color = mPaletteOrangeColor;
                    }

                    if (value >= MeasureThreshold.Ozone.MAX) {
                        color = mPaletteRedColor;
                    }
                    mOzoneValue.setText(valueString);
                    mContainerTop.setBackgroundColor(color);
                    break;
                case PM10:

                    if (value >= MeasureThreshold.PM10.MIN) {
                        color = mPaletteOrangeColor;
                    }

                    if (value >= MeasureThreshold.PM10.MAX) {
                        color = mPaletteRedColor;
                    }
                    mPmValue.setText(valueString);
                    mContainerBottom.setBackgroundColor(color);
                    break;
            }
        }

        return mIconGenerator.makeIcon();
    }
}
