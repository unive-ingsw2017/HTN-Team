package it.greenteadev.unive.gagga.injection.component;

import dagger.Subcomponent;
import it.greenteadev.unive.gagga.injection.PerActivity;
import it.greenteadev.unive.gagga.injection.module.ActivityModule;
import it.greenteadev.unive.gagga.ui.main.MainActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
