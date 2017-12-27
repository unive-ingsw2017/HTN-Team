package it.greenteadev.unive.gagga.data.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 * Created by Hitech95 on 16/10/2017.
 */

@AutoValue
public abstract class CoordsResponse {

    public abstract List<Coordinate> coordinate();

    public static CoordsResponse create(List<Coordinate> coords) {
        return new AutoValue_CoordsResponse(coords);
    }

    public static TypeAdapter<CoordsResponse> typeAdapter(Gson gson) {
        return new AutoValue_CoordsResponse.GsonTypeAdapter(gson);
    }
}
