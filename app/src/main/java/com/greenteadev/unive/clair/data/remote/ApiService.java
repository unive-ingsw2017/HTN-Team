package com.greenteadev.unive.clair.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greenteadev.unive.clair.data.LocalDateTimeTypeAdapter;
import com.greenteadev.unive.clair.data.model.CoordsResponse;
import com.greenteadev.unive.clair.data.model.MeasureResponse;
import com.greenteadev.unive.clair.data.model.StationResponse;
import com.greenteadev.unive.clair.reference.Endpoint;
import com.greenteadev.unive.clair.util.MyGsonTypeAdapterFactory;

import org.joda.time.LocalDateTime;

import io.reactivex.Observable;
import it.greenteadev.unive.clair.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ApiService {

    @GET("coords.json")
    Observable<CoordsResponse> getCoords();

    @GET("stats.json")
    Observable<StationResponse> getStations();

    @GET("data.json")
    Observable<MeasureResponse> getData();

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static ApiService newApiService() {

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                clientBuilder.addInterceptor(interceptor);
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .setDateFormat(Endpoint.DATETIMEFORMAT)
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Endpoint.API_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            return retrofit.create(ApiService.class);
        }
    }
}
