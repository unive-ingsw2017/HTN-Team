package it.greenteadev.unive.gagga.data;

import com.google.gson.Gson;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import it.greenteadev.unive.gagga.data.local.DatabaseHelper;
import it.greenteadev.unive.gagga.data.local.PreferencesHelper;
import it.greenteadev.unive.gagga.data.model.Coordinate;
import it.greenteadev.unive.gagga.data.model.CoordsResponse;
import it.greenteadev.unive.gagga.data.model.DataResponse;
import it.greenteadev.unive.gagga.data.model.Station;
import it.greenteadev.unive.gagga.data.remote.ApiService;
import it.greenteadev.unive.gagga.reference.Search;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import timber.log.Timber;

@Singleton
public class DataManager {

    private final ApiService mApiService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(ApiService apiService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mApiService = apiService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<CoordsResponse> getApiCoords() {
        return mApiService.getCoords();
    }

    public Observable<DataResponse> getApiStations() {
        return mApiService.getStations();
    }

    public Observable<DataResponse> getApiData() {
        return mApiService.getData();
    }

    public Observable<List<Station>> getStations() {
        return Observable.zip(mApiService.getCoords(), mApiService.getStations(),
                (coordsResponse, stationResponse) -> {
                    List<Station> result = new ArrayList<>();
                    List<Coordinate> coordinate = coordsResponse.coordinate();
                    List<Station> stations = stationResponse.stazioni();

                    Collections.sort(coordinate);
                    Collections.sort(stations);

                    for (Coordinate coord : coordsResponse.coordinate()) {
                        int i = Collections.binarySearch(stations, Station.builder()
                                .setCodseqst(coord.codseqst())
                                .build());

                        if (i > -1) {
                            result.add(Station.builder(stations.get(i))
                                    .setCoordinate(coord)
                                    .build());
                        }
                    }

                    return result;
                })
                .flatMap(this::cacheStations)
                .onErrorResumeNext(new Function<Throwable, Observable<List<Station>>>() {
                    @Override
                    public Observable<List<Station>> apply(Throwable e) {
                        Timber.d("onErrorResumeNext() %s", e.toString());
                        return mDatabaseHelper.getStations().distinct()
                                .flatMap(r -> DataManager.checkEmptyList(r, e));
                    }
                });
    }

    public Observable<List<Station>> cacheStations(List<Station> stations) {
        return mDatabaseHelper.setStations(stations).flatMap(
                data -> Observable.just(stations));
    }

    public Observable<List<Station>> searchStations(String name) {
        return getStations().flatMap(data -> {
            int lastScore = -1;
            List<Station> results = new ArrayList<>();

            for (Station s : data) {
                int score = FuzzySearch.tokenSortPartialRatio(name, s.nome());
                if (score >= Search.MATCH_PERCENTAGE) {
                    if (score >= lastScore) {
                        results.add(0, s);
                        lastScore = score;
                    } else results.add(s);
                }
            }

            return Observable.just(results);
        });
    }

    public Observable<Station> getStationById(String id) {
        return mApiService.getStations()
                .flatMap(dataResponse -> {
                    List<Station> stations = dataResponse.stazioni();
                    Collections.sort(stations);

                    int i = Collections.binarySearch(stations, Station.builder()
                            .setCodseqst(id)
                            .build());
                    if (i > -1)
                        return Observable.just(stations.get(i));
                    else return Observable.empty();
                })
                .onErrorResumeNext(mDatabaseHelper.getStation(id));
    }

    public static <E> Observable<List<E>> checkEmptyList(List<E> list, Throwable e) {
        if (list.size() > 0) {
            return Observable.just(list);
        }
        return Observable.error(e);
    }
}
