package com.greenteadev.unive.clair.ui.history;

import com.google.common.collect.TreeMultimap;
import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.MeasurePlotData;
import com.greenteadev.unive.clair.ui.base.BasePresenter;
import com.greenteadev.unive.clair.util.RxUtil;

import java.net.ConnectException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public class HistoryPresenter extends BasePresenter<HistoryMvpView> {

    private final DataManager mDataManager;

    private Disposable mDisposableData;

    @Inject
    public HistoryPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(HistoryMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void loadAllStationData(String id) {
        checkViewAttached();
        RxUtil.dispose(mDisposableData);
        mDataManager.getStationPlotDataById(id)
                .flatMap(response -> {
                    TreeMultimap<MeasureData.MeasureType, MeasurePlotData> map = TreeMultimap.create();

                    for (MeasurePlotData measure : response) {
                        map.put(measure.type(), measure);
                    }

                    return Observable.just(map);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TreeMultimap<MeasureData.MeasureType, MeasurePlotData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableData = d;
                    }

                    @Override
                    public void onNext(@NonNull TreeMultimap<MeasureData.MeasureType, MeasurePlotData> measureMap) {
                        getMvpView().showStationData(measureMap);
                        mDisposableData.dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error loading the station info.");
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("loadStationDataById() onComplete()");
                    }
                });
    }
}
