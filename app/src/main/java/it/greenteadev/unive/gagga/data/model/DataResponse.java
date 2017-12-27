package it.greenteadev.unive.gagga.data.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 * Created by Hitech95 on 16/10/2017.
 */

@AutoValue
public abstract class DataResponse {

    public abstract List<Station> stazioni();

    public static DataResponse create(List<Station> stazioni) {
        return new AutoValue_DataResponse(stazioni);
    }

    public static TypeAdapter<DataResponse> typeAdapter(Gson gson) {
        return new AutoValue_DataResponse.GsonTypeAdapter(gson);
    }
}
