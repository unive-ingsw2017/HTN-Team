package com.greenteadev.unive.clair.ui.history;

import com.google.common.collect.TreeMultimap;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.ui.base.MvpView;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public interface HistoryMvpView extends MvpView {

    void showStationData(TreeMultimap<MeasureData.MeasureType, MeasurePlotData> station);

    void showError(boolean networkError);
}
