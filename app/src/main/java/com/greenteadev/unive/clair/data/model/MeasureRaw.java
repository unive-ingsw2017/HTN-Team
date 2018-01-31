package com.greenteadev.unive.clair.data.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.reactivex.annotations.Nullable;

/**
 * Created by Hitech95 on 10/01/2018.
 */

@AutoValue
public abstract class MeasureRaw implements Comparable<MeasureRaw>, Parcelable {

    @SerializedName("codseqst")
    public abstract String stationId();

    @SerializedName("misurazioni")
    @Nullable
    public abstract List<MeasureContainer> raw();

    public static Builder builder() {
        return new AutoValue_MeasureRaw.Builder();
    }

    public static Builder builder(MeasureRaw measureRaw) {
        return new AutoValue_MeasureRaw.Builder(measureRaw);
    }

    public static TypeAdapter<MeasureRaw> typeAdapter(Gson gson) {
        return new AutoValue_MeasureRaw.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setStationId(String id);

        @Nullable
        public abstract Builder setRaw(List<MeasureContainer> raw);

        public abstract MeasureRaw build();
    }

    @Override
    public int compareTo(@NonNull MeasureRaw another) {
        return stationId().compareTo(another.stationId());
    }
}
