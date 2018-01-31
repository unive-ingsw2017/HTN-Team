package com.greenteadev.unive.clair.util;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import io.reactivex.annotations.Nullable;

/**
 * Created by Hitech95 on 31/01/2018.
 */

@AutoValue
public abstract class ParcelableFloat implements Parcelable {

    @Nullable
    public abstract Float value();
}
