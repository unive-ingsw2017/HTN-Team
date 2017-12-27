package it.greenteadev.unive.gagga.data.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import io.reactivex.annotations.Nullable;

@AutoValue
public abstract class Station implements Comparable<Station>, Parcelable, SearchSuggestion {

    public abstract String codseqst();

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

    /**
     * Returns the text that should be displayed
     * for the suggestion represented by this object.
     *
     * @return the text for this suggestion
     */
    public String getBody(){
        return nome();
    }

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
        public abstract Builder setCodseqst(String codseqst);

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

    @Override
    public int compareTo(@NonNull Station another) {
        return codseqst().compareTo(another.codseqst());
    }
}

