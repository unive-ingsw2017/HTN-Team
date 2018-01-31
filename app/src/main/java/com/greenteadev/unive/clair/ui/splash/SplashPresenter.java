package com.greenteadev.unive.clair.ui.splash;

import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.ui.base.BasePresenter;
import com.greenteadev.unive.clair.ui.main.MainMvpView;
import com.greenteadev.unive.clair.util.RxUtil;

import java.net.ConnectException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class SplashPresenter extends BasePresenter<SplashMvpView> {

    private final DataManager mDataManager;
    private Disposable mDisposableSynk;


    @Inject
    public SplashPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SplashMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void synkData() {
        checkViewAttached();
        RxUtil.dispose(mDisposableSynk);
        mDataManager.getStations()
                .flatMap(res -> mDataManager.getPurgedStationsData())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<MeasureData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableSynk = d;
                    }

                    @Override
                    public void onNext(@NonNull List<MeasureData> stations) {
                        getMvpView().dataReceived(true);
                        mDisposableSynk.dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error loading the stations.");
                        getMvpView().dataReceived(false);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
