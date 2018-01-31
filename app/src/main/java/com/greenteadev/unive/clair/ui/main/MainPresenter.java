package com.greenteadev.unive.clair.ui.main;

import android.util.Pair;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.model.MeasureData;
import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.data.model.StationMeasure;
import com.greenteadev.unive.clair.injection.ConfigPersistent;
import com.greenteadev.unive.clair.rxbinding.RxFloatingSearchView;
import com.greenteadev.unive.clair.ui.base.BasePresenter;
import com.greenteadev.unive.clair.util.RxUtil;

import timber.log.Timber;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager mDataManager;
    private Disposable mDisposableSynk;
    private Disposable mDisposableData;
    private Disposable mDisposableSearch;
    private Disposable mDisposableBookmark;

    private FloatingSearchView mMvpToolbarView;

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposableSynk != null) mDisposableSynk.dispose();
        if (mDisposableData != null) mDisposableData.dispose();
        if (mDisposableBookmark != null) mDisposableBookmark.dispose();
    }

    public void attachToolbarView(FloatingSearchView toolbar) {
        mMvpToolbarView = toolbar;
        RxFloatingSearchView.queryChanges(toolbar)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(charSequence -> charSequence.length() > 2)
                .debounce(250, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(dataResponse -> searchStationByName(dataResponse, true), Throwable::printStackTrace);
    }

    public void detachToolbarView() {
        mMvpToolbarView = null;
        if (mDisposableSearch != null) mDisposableSearch.dispose();
    }

    public boolean isToolbarViewAttached() {
        return mMvpToolbarView != null;
    }

    public FloatingSearchView getMvpToolbarView() {
        return mMvpToolbarView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public void checkToolbarViewAttached() {
        if (!isToolbarViewAttached()) throw new MvpViewNotAttachedException();
    }

    public void loadStations() {
        checkViewAttached();
        RxUtil.dispose(mDisposableSynk);
        mDataManager.getStationsWithPlotData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StationMeasure>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableSynk = d;
                    }

                    @Override
                    public void onNext(@NonNull List<StationMeasure> stations) {
                        if (stations.isEmpty()) {
                            getMvpView().showStationsEmpty();
                        } else {
                            getMvpView().showStations(stations);
                        }
                        mDisposableSynk.dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error loading the stations.");
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void loadStationDataById(String id) {
        checkViewAttached();
        RxUtil.dispose(mDisposableData);
        mDataManager.getStationEPlotDataById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<StationMeasure>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableData = d;
                    }

                    @Override
                    public void onNext(@NonNull StationMeasure measureRaw) {
                        getMvpView().showStationData(measureRaw);
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

    public void disposeLoadStation() {
        RxUtil.dispose(mDisposableData);
    }

    public void searchStationByName(String name, boolean suggestion) {
        checkToolbarViewAttached();
        RxUtil.dispose(mDisposableSearch);
        mDataManager.searchStations(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Station>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableSearch = d;
                        if (getMvpToolbarView().isSearchBarFocused())
                            getMvpToolbarView().showProgress();
                    }

                    @Override
                    public void onNext(@NonNull List<Station> stations) {
                        getMvpView().showSearchResult(stations, suggestion);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error loading the station suggestion.");
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {
                        getMvpToolbarView().hideProgress();
                    }
                });
    }

    public void disposeSearchStation() {
        RxUtil.dispose(mDisposableSearch);
    }

    public void isBookmarkStation(String stationId) {
        checkViewAttached();
        RxUtil.dispose(mDisposableBookmark);
        mDataManager.isStationBookmark(stationId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableBookmark = d;
                        getMvpView().showBookmark(false);
                    }

                    @Override
                    public void onNext(@NonNull Boolean result) {
                        getMvpView().showBookmark(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getMvpView().showBookmark(false);
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void bookmarkStation(String stationId, boolean bookmark) {
        checkViewAttached();
        RxUtil.dispose(mDisposableBookmark);
        mDataManager.setStationBookmark(stationId, bookmark)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableBookmark = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean result) {
                        if (!result)
                            getMvpView().showBookmark(!bookmark);
                        else
                            getMvpView().showBookmark(bookmark);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error bookmarking the station.");
                        getMvpView().showBookmark(!bookmark);
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
