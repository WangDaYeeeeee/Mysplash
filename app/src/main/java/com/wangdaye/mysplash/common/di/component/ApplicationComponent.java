package com.wangdaye.mysplash.common.di.component;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.di.module.ApplicationModule;
import com.wangdaye.mysplash.common.di.module.PresenterModule;
import com.wangdaye.mysplash.common.di.module.ViewModelFactoryModule;
import com.wangdaye.mysplash.common.di.module.ActivityModule;
import com.wangdaye.mysplash.common.di.module.FragmentModule;
import com.wangdaye.mysplash.common.di.module.RepositoryModule;
import com.wangdaye.mysplash.common.di.module.NetworkModule;
import com.wangdaye.mysplash.common.di.module.ViewModelModule;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidInjectionModule.class, AndroidSupportInjectionModule.class,
        ApplicationModule.class, ActivityModule.class, FragmentModule.class, ViewModelFactoryModule.class,
        RepositoryModule.class, NetworkModule.class, ViewModelModule.class, PresenterModule.class})
public interface ApplicationComponent {

    void inject(Mysplash application);
}
