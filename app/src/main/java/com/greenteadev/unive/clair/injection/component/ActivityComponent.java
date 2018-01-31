package com.greenteadev.unive.clair.injection.component;

import com.greenteadev.unive.clair.injection.PerActivity;
import com.greenteadev.unive.clair.injection.module.ActivityModule;
import com.greenteadev.unive.clair.ui.about.AboutActivity;
import com.greenteadev.unive.clair.ui.history.HistoryActivity;
import com.greenteadev.unive.clair.ui.main.MainActivity;
import com.greenteadev.unive.clair.ui.settings.SettingsBookmarkActivity;
import com.greenteadev.unive.clair.ui.splash.SplashActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(MainActivity mainActivity);

    void inject(HistoryActivity historyActivity);

    void inject(AboutActivity aboutActivity);

    void inject(SettingsBookmarkActivity bookmarkedStationActivity);

}
