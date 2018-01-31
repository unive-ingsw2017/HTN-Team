package com.greenteadev.unive.clair.ui.settings;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.net.ConnectException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.model.Station;
import com.greenteadev.unive.clair.ui.base.BasePresenter;
import com.greenteadev.unive.clair.util.RxUtil;

import timber.log.Timber;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public class BookmarkPresenter extends BasePresenter<BookmarkMvpView> {
    private final DataManager mDataManager;

    private Disposable mDisposableBookmark;
    private Disposable mDisposableBookmarkDelete;

    private FloatingSearchView mMvpToolbarView;

    @Inject
    public BookmarkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(BookmarkMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mDisposableBookmark != null) mDisposableBookmark.dispose();
        if (mDisposableBookmarkDelete != null) mDisposableBookmarkDelete.dispose();
    }

    public void loadBookmarkedStations() {
        checkViewAttached();
        RxUtil.dispose(mDisposableBookmark);
        mDataManager.getStationsBookmarked()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Station>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableBookmark = d;
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

    public void bookmarkStation(String stationId, boolean status) {
        checkViewAttached();
        RxUtil.dispose(mDisposableBookmarkDelete);
        mDataManager.setStationBookmark(stationId, status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposableBookmarkDelete = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean result) {
                        getMvpView().showBookmarkRemoved(result, status);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e, "There was an error bookmarking the station.");
                        getMvpView().showBookmarkRemoved(false, status);
                        getMvpView().showError(e instanceof ConnectException);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
