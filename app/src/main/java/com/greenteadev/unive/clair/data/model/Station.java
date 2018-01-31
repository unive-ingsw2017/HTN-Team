package com.greenteadev.unive.clair.data.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import io.reactivex.annotations.Nullable;

@AutoValue
public abstract class Station implements Comparable<Station>, Parcelable, SearchSuggestion, ClusterItem {

    @SerializedName("codseqst")
    public abstract String id();

    @Nullable
    public abstract String nome();

    @Nullable
    public abstract String localita();

    @Nullable
    public abstract String comune();

    @Nullable
    public abstract String provincia();

    @Nullable
    public abstract String tipozona();

    @Nullable
    public abstract Coordinate coordinate();

    public static Builder builder() {
        return new AutoValue_Station.Builder();
    }

    public static Builder builder(Station station) {
        return new AutoValue_Station.Builder(station);
    }

    public static TypeAdapter<Station> typeAdapter(Gson gson) {
        return new AutoValue_Station.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(String codseqst);

        @Nullable
        public abstract Builder setNome(String nome);

        @Nullable
        public abstract Builder setLocalita(String localita);

        @Nullable
        public abstract Builder setComune(String comune);

        @Nullable
        public abstract Builder setProvincia(String provincia);

        @Nullable
        public abstract Builder setTipozona(String tipozona);

        @Nullable
        public abstract Builder setCoordinate(Coordinate coordinate);

        public abstract Station build();
    }

    /**
     * Returns the text that should be displayed
     * for the suggestion represented by this object.
     *
     * @return the text for this suggestion
     */
    @Override
    public String getBody(){
        return nome();
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(coordinate().lat()),
                Double.parseDouble(coordinate().lon()));
    }

    @Override
    public int compareTo(@NonNull Station another) {
        return id().compareTo(another.id());
    }
}

