package com.greenteadev.unive.clair.rxbinding;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.jakewharton.rxbinding2.InitialValueObservable;

public final class RxFloatingSearchView {

    @CheckResult
    @NonNull
    public static InitialValueObservable<String> queryChanges(
            @NonNull FloatingSearchView view) {
        return queryChanges(view, 3);
    }

    @CheckResult
    @NonNull
    public static InitialValueObservable<String> queryChanges(
            @NonNull FloatingSearchView view, int characterLimit) {
        checkNotNull(view, "view == null");
        return new QueryObservable(view, characterLimit);
    }

    public static void checkNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}

