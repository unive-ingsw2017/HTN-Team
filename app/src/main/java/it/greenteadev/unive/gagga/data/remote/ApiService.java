package it.greenteadev.unive.gagga.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import it.greenteadev.unive.gagga.BuildConfig;
import it.greenteadev.unive.gagga.data.model.CoordsResponse;
import it.greenteadev.unive.gagga.data.model.DataResponse;
import it.greenteadev.unive.gagga.reference.Endpoint;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import it.greenteadev.unive.gagga.util.MyGsonTypeAdapterFactory;

public interface ApiService {

    @GET("coords.json")
    Observable<CoordsResponse> getCoords();

    @GET("stats.json")
    Observable<DataResponse> getStations();

    @GET("data.json")
    Observable<DataResponse> getData();

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
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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
