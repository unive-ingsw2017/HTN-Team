package com.greenteadev.unive.clair.injection.module;

import android.app.Application;
import android.content.Context;

import com.greenteadev.unive.clair.ClairApplication;
import com.greenteadev.unive.clair.data.remote.ApiService;
import com.greenteadev.unive.clair.injection.ApplicationContext;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
        return ((ClairApplication) mApplication).getReferenceWatcher();
    }

}
