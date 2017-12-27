package it.greenteadev.unive.gagga.ui.main;

import java.util.List;

import it.greenteadev.unive.gagga.data.model.Station;
import it.greenteadev.unive.gagga.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showStations(List<Station> stations);

    void showStationsEmpty();

    void showStation(Station station);

    void showStationEmpty();

    void showStationsResult(List<Station> stations, boolean suggestion);

    void showError(boolean networkError);
}
