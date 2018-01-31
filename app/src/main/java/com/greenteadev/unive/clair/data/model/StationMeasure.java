package com.greenteadev.unive.clair.data.model;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.auto.value.AutoValue;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

import io.reactivex.annotations.Nullable;

/**
 * Created by Hitech95 on 10/01/2018.
 */

@AutoValue
public abstract class StationMeasure implements Parcelable, ClusterItem {

    public abstract Station station();

    @Nullable
    public abstract List<MeasurePlotData> measures();


    public static StationMeasure.Builder builder() {
        return new AutoValue_StationMeasure.Builder();
    }

    public static Builder builder(StationMeasure measure) {
        return new AutoValue_StationMeasure.Builder(measure);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder setStation(Station station);

        @Nullable
        public abstract Builder setMeasures(List<MeasurePlotData> measures);

        public abstract StationMeasure build();
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(station().coordinate().lat()),
                Double.parseDouble(station().coordinate().lon()));
    }
}
