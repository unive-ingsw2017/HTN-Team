package com.greenteadev.unive.clair.data;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.greenteadev.unive.clair.ClairApplication;

import javax.inject.Inject;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class SynkJob extends Job {
    public static final String TAG = "job_synk_tag";

    public static final float WINDOW_MULTIPLIER = 1.2f;

    @Inject
    DataManager mDataManager;

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        ClairApplication.get(getContext()).getComponent().inject(this);

        Intent service = new Intent(getContext(), SynkJob.class);
        getContext().startService(service);

        if (mDataManager.getPreferencesHelper().synkInterval() > 0) {
            long ms = mDataManager.getPreferencesHelper().synkInterval() * 60 * 1000;
            scheduleJob(ms, (long) (ms * WINDOW_MULTIPLIER));
        }

        return Result.SUCCESS;
    }

    public static int scheduleJob(long time, long multiplier) {
        return new JobRequest.Builder(SynkJob.TAG)
                .setExecutionWindow(time, multiplier)
                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
}
