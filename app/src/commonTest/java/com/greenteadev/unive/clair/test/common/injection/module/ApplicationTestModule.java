package com.greenteadev.unive.clair.test.common.injection.module;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.greenteadev.unive.clair.data.DataManager;
import com.greenteadev.unive.clair.data.remote.ApiService;
import com.greenteadev.unive.clair.injection.ApplicationContext;

import static org.mockito.Mockito.mock;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
public class ApplicationTestModule {

    private final Application mApplication;

    public ApplicationTestModule(Application application) {
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

    /************* MOCKS *************/

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return mock(DataManager.class);
    }

    @Provides
    @Singleton
    ApiService provideRibotsService() {
        return mock(ApiService.class);
    }

    @Provides
    @Singleton
    RefWatcher provideLeakCanary() {
        return null;
    }
}
