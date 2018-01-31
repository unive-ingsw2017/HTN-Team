package com.greenteadev.unive.clair.ui.main;

import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.data.model.StationMeasure;
import com.greenteadev.unive.clair.ui.base.MvpView;

import java.util.List;

public interface MainMvpView extends MvpView {

    void showStations(List<StationMeasure> stations);

    void showStationsEmpty();

    void showStationData(StationMeasure station);

    void showStationDataEmpty();

    void showSearchResult(List<Station> stations, boolean suggestion);

    void showBookmark(Boolean result);

    void showError(boolean networkError);
}
