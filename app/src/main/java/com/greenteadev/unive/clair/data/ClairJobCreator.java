package com.greenteadev.unive.clair.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class ClairJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case ClearDataJob.TAG:
                return new ClearDataJob();
            case SynkJob.TAG:
                return new SynkJob();
            default:
                return null;
        }
    }
}
