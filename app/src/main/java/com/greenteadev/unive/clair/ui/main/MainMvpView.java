package com.greenteadev.unive.clair.ui.main;

import java.util.List;

import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.data.model.StationMeasure;
import com.greenteadev.unive.clair.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showStations(List<StationMeasure> stations);

    void showStationsEmpty();

    void showStationData(StationMeasure station);

    void showStationDataEmpty();

    void showSearchResult(List<Station> stations, boolean suggestion);

    void showBookmark(Boolean result);

    void showError(boolean networkError);
}
