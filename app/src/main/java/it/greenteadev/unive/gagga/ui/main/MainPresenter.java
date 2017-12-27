package it.greenteadev.unive.gagga.ui.main;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.gson.Gson;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.greenteadev.unive.gagga.data.DataManager;
import it.greenteadev.unive.gagga.data.model.Station;
import it.greenteadev.unive.gagga.injection.ConfigPersistent;
import it.greenteadev.unive.gagga.rxbinding.RxFloatingSearchView;
import it.greenteadev.unive.gagga.ui.base.BasePresenter;
import it.greenteadev.unive.gagga.util.RxUtil;
import timber.log.Timber;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager mDataManager;
    private Disposable mDisposableSynk;
    private Disposable mDisposableData;
    private Disposable mDisposableSearch;

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
        mDataManager.getStations()
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Station>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableSynk = d;
                    }

                    @Override
                    public void onNext(@NonNull List<Station> stations) {
                        if (stations.isEmpty()) {
                            getMvpView().showStationsEmpty();
                        } else {
                            getMvpView().showStations(stations);
                        }
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

    public void loadStationById(String id) {
        checkViewAttached();
        RxUtil.dispose(mDisposableData);
        mDataManager.getStationById(id)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Station>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableData = d;
                    }

                    @Override
                    public void onNext(@NonNull Station station) {
                        if (station == null) {
                            getMvpView().showStationEmpty();
                        } else {
                            getMvpView().showStation(station);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error loading the station info.");
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {

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
                        getMvpView().showStationsResult(stations, suggestion);
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
}
