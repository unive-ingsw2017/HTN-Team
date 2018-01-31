package com.greenteadev.unive.clair.data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

import com.google.common.collect.TreeMultimap;
import com.greenteadev.unive.clair.data.local.DatabaseHelper;
import com.greenteadev.unive.clair.data.local.PreferencesHelper;
import com.greenteadev.unive.clair.data.model.Coordinate;
import com.greenteadev.unive.clair.data.model.CoordsResponse;
import com.greenteadev.unive.clair.data.model.MeasureContainer;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.data.model.MeasureRaw;
import com.greenteadev.unive.clair.data.model.MeasureResponse;
import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.data.model.StationMeasure;
import com.greenteadev.unive.clair.data.model.StationResponse;
import com.greenteadev.unive.clair.data.remote.ApiService;
import com.greenteadev.unive.clair.reference.Search;

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

    public Observable<StationResponse> getApiStations() {
        return mApiService.getStations();
    }

    public Observable<MeasureResponse> getApiData() {
        return mApiService.getData();
    }

    public Observable<List<Station>> getStations() {
        Timber.d("getStations()");
        return Observable.zip(mApiService.getCoords(), mApiService.getStations(),
                (coordsResponse, stationResponse) -> {
                    List<Station> result = new ArrayList<>();
                    List<Coordinate> coordinate = coordsResponse.coordinate();
                    List<Station> stations = stationResponse.stations();

                    Collections.sort(coordinate);
                    Collections.sort(stations);

                    for (Coordinate coord : coordsResponse.coordinate()) {
                        int i = Collections.binarySearch(stations, Station.builder()
                                .setId(coord.codseqst())
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
                .onErrorResumeNext(e -> {
                    return mDatabaseHelper.getStations().distinct()
                            .flatMap(r -> DataManager.checkEmpty(r, e));
                });
    }

    public Observable<List<Station>> cacheStations(List<Station> stations) {
        return mDatabaseHelper.setStations(stations).flatMap(
                data -> Observable.just(stations));
    }

    public Observable<List<Station>> searchStations(String name) {
        Timber.d("searchStations()");
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

    public Observable<Station> getStationById(String stationId) {
        Thread.dumpStack();
        Timber.d("getStationById()");
        return getStations()
                .flatMap(stationResponse -> {
                    Collections.sort(stationResponse);

                    int i = Collections.binarySearch(stationResponse, Station.builder()
                            .setId(stationId)
                            .build());
                    if (i > -1)
                        return Observable.just(stationResponse.get(i));
                    else return Observable.empty();
                })
                .onErrorResumeNext(mDatabaseHelper.getStation(stationId));
    }

    public Observable<Boolean> isStationBookmark(String stationId) {

        return mDatabaseHelper.isStationBookmark(stationId)
                .flatMap(bookmarkResponse -> {
                    if (bookmarkResponse != null && bookmarkResponse.length() > 0)
                        return Observable.just(true);
                    else return Observable.empty();
                });
    }

    public Observable<Boolean> setStationBookmark(String stationId, boolean bookmark) {
        return (bookmark) ? mDatabaseHelper.addStationBookmark(stationId) :
                mDatabaseHelper.removeStationBookmark(stationId);
    }

    public Observable<List<Station>> getStationsBookmarked() {
        return mDatabaseHelper.getStationsBookmarked();
    }

    public Observable<List<MeasureData>> getPurgedStationsData() {
        return mApiService.getData()
                .flatMap(dataResponse -> {
                    List<MeasureData> measuresMap = new ArrayList<>();

                    List<MeasureRaw> measuresRawList = dataResponse.measures();
                    Collections.sort(measuresRawList);

                    for (MeasureRaw measureRaw : measuresRawList) {
                        if (measureRaw.raw() != null) {

                            for (MeasureContainer container : measureRaw.raw()) {
                                List<MeasureData> measures = new ArrayList<>();
                                List<MeasureData> measuresCorrect = new ArrayList<>();
                                MeasureData.MeasureType type = MeasureData.MeasureType.UNKNOWN;

                                if (container.ozone() != null && !container.ozone().isEmpty()) {
                                    measures = container.ozone();
                                    type = MeasureData.MeasureType.OZONE;
                                } else if (container.pm10() != null && !container.pm10().isEmpty()) {
                                    measures = container.pm10();
                                    type = MeasureData.MeasureType.PM10;
                                }

                                for (MeasureData measure : measures) {
                                    try {
                                        measuresCorrect.add(MeasureData.builder(measure)
                                                .setType(type)
                                                .setValueNum(Float.parseFloat(measure.value()))
                                                .setStationId(measureRaw.stationId())
                                                .build());
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                    }
                                }

                                Collections.sort(measuresCorrect);
                                measuresMap.addAll(measuresCorrect);
                            }
                        }
                    }

                    return Observable.just(measuresMap);
                })
                .flatMap(list -> cacheStationData(list))
                .onErrorResumeNext(e -> {
                    Timber.d("onErrorResumeNext() %s", e.toString());
                    return mDatabaseHelper.getStationData()
                            .flatMap(r -> DataManager.checkEmptyList(r, e));
                });
    }

    public Observable<List<MeasureData>> cacheStationData(List<MeasureData> dataList) {
        return mDatabaseHelper.setStationData(dataList).flatMap(
                data -> Observable.just(dataList));
    }

    public Observable<List<MeasureData>> getStationDataById(String stationId) {
        return getPurgedStationsData()
                .flatMap(cleanDBData -> mDatabaseHelper.getStationData(stationId));
    }

    public Observable<List<MeasurePlotData>> getStationPlotDataById(String stationId) {
        return getPurgedStationsData()
                .flatMap(cleanDBData -> mDatabaseHelper.getPlotStationData(stationId));
    }

    public Observable<StationMeasure> getStationEPlotDataById(String stationId) {
        return getPurgedStationsData()
                .flatMap(response -> Observable.zip(getStationById(stationId),
                        mDatabaseHelper.getPlotStationData(stationId),
                        (stationResponse, dataResponse) -> StationMeasure.builder()
                                .setStation(stationResponse)
                                .setMeasures(dataResponse)
                                .build()));
    }

    public Observable<List<StationMeasure>> getStationsWithPlotData() {
        return Observable.zip(mDatabaseHelper.getStations(), mDatabaseHelper.getStationData(), (stations, dataList) -> {
            List<StationMeasure> resultList = new ArrayList<>();

            for (Station s : stations) {
                List<MeasurePlotData> lastMeasures = new ArrayList<>();
                TreeMultimap<MeasureData.MeasureType, MeasureData> measuresMap = TreeMultimap.create();

                for (MeasureData measureData : dataList) {
                    if (measureData.stationId().equals(s.id())) {
                        measuresMap.put(measureData.type(), measureData);
                    }
                }

                for (MeasureData.MeasureType key : measuresMap.keySet()) {
                    List<MeasureData> measureData = new ArrayList<>(measuresMap.get(key));
                    MeasureData measure = measureData.get(measureData.size() - 1);

                    lastMeasures.add(MeasurePlotData.builder()
                            .setDate(measure.date().toLocalDate())
                            .setType(measure.type())
                            .setAvg(measure.valueNum())
                            .build());
                }

                resultList.add(StationMeasure.builder().setStation(s).setMeasures(lastMeasures).build());
            }

            return resultList;
        });
    }

    public Observable<Boolean> clearStations() {
        return mDatabaseHelper.clearStation();
    }

    public Observable<Boolean> clearStationData() {
        return mDatabaseHelper.clearStationData();
    }

    public static <E> Observable<E> checkEmpty(E element, Throwable e) {
        if (element != null) {
            return Observable.just(element);
        }
        return Observable.error(e);
    }

    public static <E> Observable<List<E>> checkEmptyList(List<E> list, Throwable e) {
        if (list.size() > 0) {
            return Observable.just(list);
        }
        return Observable.error(e);
    }
}
