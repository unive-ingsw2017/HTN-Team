package com.greenteadev.unive.clair.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDateTime;

import io.reactivex.annotations.Nullable;

/**
 * Created by Hitech95 on 10/01/2018.
 */

@AutoValue
public abstract class MeasureData implements Comparable<MeasureData>, Parcelable {

    public enum MeasureType {
        UNKNOWN,
        @SerializedName("ozone")OZONE,
        @SerializedName("pm10")PM10
    }

    @Nullable
    public abstract String stationId();

    @SerializedName("data")
    public abstract LocalDateTime date();

    @SerializedName("mis")
    @Nullable
    public abstract String value();

    @Nullable
    public abstract Float valueNum();

    @Nullable
    public abstract MeasureType type();


    public static Builder builder() {
        return new AutoValue_MeasureData.Builder();
    }

    public static Builder builder(MeasureData measureData) {
        return new AutoValue_MeasureData.Builder(measureData);
    }

    public static TypeAdapter<MeasureData> typeAdapter(Gson gson) {
        return new AutoValue_MeasureData.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setDate(LocalDateTime date);

        @Nullable
        public abstract Builder setStationId(String stationId);

        @Nullable
        public abstract Builder setValue(String value);

        @Nullable
        public abstract Builder setValueNum(Float value);

        @Nullable
        public abstract Builder setType(MeasureType type);

        public abstract MeasureData build();
    }

    public int compareTo(MeasureData another) {
        int ret = date().compareTo(another.date());

        if (ret == 0) {
            ret = type().compareTo(another.type());
        }

        if (ret == 0) {
            ret = value().compareTo(another.value());
        }

        return ret;
    }
}