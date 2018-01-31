package com.greenteadev.unive.clair.data;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.greenteadev.unive.clair.ClairApplication;
import com.greenteadev.unive.clair.util.RxUtil;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class ClearDataJob extends Job {
    public static final String TAG = "job_clear_tag";

    @Inject
    DataManager mDataManager;

    private Disposable mDisposable;

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        ClairApplication.get(getContext()).getComponent().inject(this);

        Intent service = new Intent(getContext(), SynkJob.class);
        getContext().startService(service);

        Timber.i("Starting data cleaning...");

        RxUtil.dispose(mDisposable);
        mDataManager.clearStationData()
                .flatMap(r -> mDataManager.clearStationData())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Boolean measureDataList) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.w(e, "Error clear.");
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("Clear successfully!");
                    }
                });

        return Result.SUCCESS;
    }

    public static int scheduleJob() {
        return new JobRequest.Builder(ClearDataJob.TAG)
                .startNow()
                .build()
                .schedule();
    }
}
