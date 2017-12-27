package it.greenteadev.unive.gagga.injection.module;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.greenteadev.unive.gagga.GaggaApplication;
import it.greenteadev.unive.gagga.data.remote.ApiService;
import it.greenteadev.unive.gagga.injection.ApplicationContext;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    ApiService provideRibotsService() {
        return ApiService.Creator.newApiService();
    }

    @Provides
    @Singleton
    RefWatcher provideLeakCanary() {
        return ((GaggaApplication) mApplication).getReferenceWatcher();
    }

}
