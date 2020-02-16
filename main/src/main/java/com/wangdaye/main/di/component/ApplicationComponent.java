package com.wangdaye.main.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.main.MainActivity;
import com.wangdaye.main.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);
}
