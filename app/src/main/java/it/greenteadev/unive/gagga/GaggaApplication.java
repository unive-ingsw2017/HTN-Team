package it.greenteadev.unive.gagga;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import it.greenteadev.unive.gagga.injection.component.ApplicationComponent;
import it.greenteadev.unive.gagga.injection.component.DaggerApplicationComponent;
import it.greenteadev.unive.gagga.injection.module.ApplicationModule;

public class GaggaApplication extends Application {

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
    }

    public static GaggaApplication get(Context context) {
        return (GaggaApplication) context.getApplicationContext();
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
