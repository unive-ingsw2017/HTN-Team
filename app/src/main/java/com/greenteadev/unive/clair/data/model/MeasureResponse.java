package com.greenteadev.unive.clair.data.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hitech95 on 10/01/2018.
 */

@AutoValue
public abstract class MeasureResponse {

    @SerializedName("stazioni")
    public abstract List<MeasureRaw> measures();

    public static MeasureResponse create(List<MeasureRaw> measureRaws) {
        return new AutoValue_MeasureResponse(measureRaws);
    }

    public static TypeAdapter<MeasureResponse> typeAdapter(Gson gson) {
        return new AutoValue_MeasureResponse.GsonTypeAdapter(gson);
    }
}
