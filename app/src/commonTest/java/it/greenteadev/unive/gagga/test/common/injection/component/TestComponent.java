package it.greenteadev.unive.gagga.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import it.greenteadev.unive.gagga.injection.component.ApplicationComponent;
import it.greenteadev.unive.gagga.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
