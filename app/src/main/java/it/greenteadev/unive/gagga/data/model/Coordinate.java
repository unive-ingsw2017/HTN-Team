package it.greenteadev.unive.gagga.data.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Coordinate implements Comparable<Coordinate>, Parcelable {

    public abstract String codseqst();
    public abstract String lat();
    public abstract String lon();

    public static Coordinate create(String codseqst, String lat, String lon) {
        return new AutoValue_Coordinate(codseqst, lat, lon);
    }

    public static TypeAdapter<Coordinate> typeAdapter(Gson gson) {
        return new AutoValue_Coordinate.GsonTypeAdapter(gson);
    }

    @Override
    public int compareTo(@NonNull Coordinate another) {
        return codseqst().compareTo(another.codseqst());
    }
}

