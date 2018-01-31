package com.greenteadev.unive.clair.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import org.joda.time.LocalDate;

import io.reactivex.annotations.Nullable;

/**
 * Created by Hitech95 on 25/01/2018.
 */

@AutoValue
public abstract class MeasurePlotData implements Comparable<MeasurePlotData>, Parcelable {

    public abstract LocalDate date();

    public abstract MeasureData.MeasureType type();

    @Nullable
    public abstract Float avg();

    @Nullable
    public abstract Float min();

    @Nullable
    public abstract Float max();


    public static Builder builder() {
        return new AutoValue_MeasurePlotData.Builder();
    }

    public static Builder builder(MeasurePlotData measureData) {
        return new AutoValue_MeasurePlotData.Builder(measureData);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setDate(LocalDate date);

        @Nullable
        public abstract Builder setType(MeasureData.MeasureType type);

        @Nullable
        public abstract Builder setAvg(Float value);

        @Nullable
        public abstract Builder setMin(Float value);

        @Nullable
        public abstract Builder setMax(Float value);

        public abstract MeasurePlotData build();
    }

    public int compareTo(MeasurePlotData another) {
        int ret = date().compareTo(another.date());

        if (ret == 0) {
            ret = type().compareTo(another.type());
        }

        if (ret == 0) {
            ret = avg().compareTo(another.avg());
        }

        if (ret == 0) {
            ret = min().compareTo(another.min());
        }

        if (ret == 0) {
            ret = max().compareTo(another.max());
        }

        return ret;
    }
}
