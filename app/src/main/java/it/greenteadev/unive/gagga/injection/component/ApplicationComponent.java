package it.greenteadev.unive.gagga.injection.component;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Component;
import it.greenteadev.unive.gagga.data.DataManager;
import it.greenteadev.unive.gagga.data.local.DatabaseHelper;
import it.greenteadev.unive.gagga.data.local.PreferencesHelper;
import it.greenteadev.unive.gagga.data.remote.ApiService;
import it.greenteadev.unive.gagga.injection.ApplicationContext;
import it.greenteadev.unive.gagga.injection.module.ApplicationModule;
import it.greenteadev.unive.gagga.util.RxEventBus;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    //void inject(SyncService syncService);

    @ApplicationContext Context context();
    Application application();
    ApiService apisService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    RxEventBus eventBus();
    RefWatcher refWatcher();

}
