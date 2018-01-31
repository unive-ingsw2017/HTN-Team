package com.greenteadev.unive.clair.test.common.injection.component;

import com.greenteadev.unive.clair.injection.component.ApplicationComponent;

import javax.inject.Singleton;

import dagger.Component;
import it.greenteadev.unive.clair.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
