package com.greenteadev.unive.clair;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.greenteadev.unive.clair.data.ClairJobCreator;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import it.greenteadev.unive.clair.BuildConfig;
import it.greenteadev.unive.clair.R;

import com.greenteadev.unive.clair.injection.component.ApplicationComponent;
import com.greenteadev.unive.clair.injection.component.DaggerApplicationComponent;
import com.greenteadev.unive.clair.injection.module.ApplicationModule;
import timber.log.Timber;

public class ClairApplication extends Application {

    ApplicationComponent mApplicationComponent;
    RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            //Fabric.with(this, new Crashlytics());
        }

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        mRefWatcher = LeakCanary.install(this);

        Iconify.with(new MaterialModule());
        JodaTimeAndroid.init(this);

        JobManager.create(this).addJobCreator(new ClairJobCreator());
    }

    public static ClairApplication get(Context context) {
        return (ClairApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    public RefWatcher getReferenceWatcher(){
        return mRefWatcher;
    }
}
