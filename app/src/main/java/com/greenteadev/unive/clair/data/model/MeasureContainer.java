package com.greenteadev.unive.clair.data.model;

import android.os.Parcelable;

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
public abstract class MeasureContainer implements Parcelable {

    @SerializedName("ozono")
    @Nullable
    public abstract List<MeasureData> ozone();

    @SerializedName("pm10")
    @Nullable
    public abstract List<MeasureData> pm10();

    public static Builder builder() {
        return new AutoValue_MeasureContainer.Builder();
    }

    public static Builder builder(MeasureContainer measureContainer) {
        return new AutoValue_MeasureContainer.Builder(measureContainer);
    }

    public static TypeAdapter<MeasureContainer> typeAdapter(Gson gson) {
        return new AutoValue_MeasureContainer.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setOzone(List<MeasureData> ozone);

        public abstract Builder setPm10(List<MeasureData> pm10);

        public abstract MeasureContainer build();
    }
}
