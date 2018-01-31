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
public abstract class StationResponse{

    @SerializedName("stazioni")
    public abstract List<Station> stations();

    public static StationResponse create(List<Station> stazioni) {
        return new AutoValue_StationResponse(stazioni);
    }

    public static TypeAdapter<StationResponse> typeAdapter(Gson gson) {
        return new AutoValue_StationResponse.GsonTypeAdapter(gson);
    }
}
