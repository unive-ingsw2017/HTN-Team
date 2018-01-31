package com.greenteadev.unive.clair.ui.settings;

import java.util.List;

import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.ui.base.MvpView;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public interface BookmarkMvpView extends MvpView{
    void showStations(List<Station> stations);

    void showStationsEmpty();

    void showError(boolean networkError);

    void showBookmarkRemoved(Boolean result, boolean status);
}
