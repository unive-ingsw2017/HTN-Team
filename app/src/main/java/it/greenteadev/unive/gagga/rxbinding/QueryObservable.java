package it.greenteadev.unive.gagga.rxbinding;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.jakewharton.rxbinding2.InitialValueObservable;

import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;

final public class QueryObservable extends InitialValueObservable<String> {

    private final FloatingSearchView mView;
    private final int mMinQueryDiff;

    public QueryObservable(FloatingSearchView view) {
        this(view, 3);
    }

    public QueryObservable(FloatingSearchView view, int minQueryDiff) {
        mView = view;
        mMinQueryDiff = minQueryDiff;
    }

    @Override
    protected void subscribeListener(Observer<? super String> observer) {
        if (!checkMainThread(observer)) {
            return;
        }

        Listener listener = new Listener(mView, observer, mMinQueryDiff);
        mView.setOnQueryChangeListener(listener);
        observer.onSubscribe(listener);
    }

    @Override
    protected String getInitialValue() {
        return mView.getQuery();
    }

    final static class Listener extends MainThreadDisposable implements FloatingSearchView.OnQueryChangeListener {

        private final FloatingSearchView mView;
        private final int mMinQueryDiff;
        private final Observer<? super String> mObserver;

        public Listener(FloatingSearchView view, Observer<? super String> observer, int minQueryDiff) {
            mView = view;
            mMinQueryDiff = minQueryDiff;
            mObserver = observer;
        }

        @Override
        public void onSearchTextChanged(String oldQuery, String query) {
            if (isDisposed() || query == null || query.length() < mMinQueryDiff)
                return;

            mObserver.onNext(query);
        }

        @Override
        protected void onDispose() {
            mView.setOnQueryChangeListener(null);
        }
    }
}