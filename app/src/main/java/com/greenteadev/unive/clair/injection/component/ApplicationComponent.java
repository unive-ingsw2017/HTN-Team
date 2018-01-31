package com.greenteadev.unive.clair.injection.component;

import android.app.Application;
import android.content.Context;

import com.greenteadev.unive.clair.data.ClearDataJob;
import com.greenteadev.unive.clair.data.SynkJob;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Component;
import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.SyncService;
import com.greenteadev.unive.clair.data.local.DatabaseHelper;
import com.greenteadev.unive.clair.data.local.PreferencesHelper;
import com.greenteadev.unive.clair.data.remote.ApiService;
import com.greenteadev.unive.clair.injection.ApplicationContext;
import com.greenteadev.unive.clair.injection.module.ApplicationModule;
import com.greenteadev.unive.clair.util.RxEventBus;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    // Services
    void inject(SyncService syncService);

    // Jobs
    void inject(SynkJob synkJob);

    void inject(ClearDataJob clearDataJob);

    @ApplicationContext Context context();
    Application application();
    ApiService apisService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();
    RefWatcher refWatcher();

}
