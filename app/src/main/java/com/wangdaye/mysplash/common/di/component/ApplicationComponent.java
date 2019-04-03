package com.wangdaye.mysplash.common.di.component;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.di.module.android.ActivityModule;
import com.wangdaye.mysplash.common.di.module.android.FragmentModule;
import com.wangdaye.mysplash.common.di.module.NetworkModule;
import com.wangdaye.mysplash.common.di.module.android.ViewModelModule;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidInjectionModule.class, AndroidSupportInjectionModule.class,
        ActivityModule.class, FragmentModule.class, ViewModelModule.class,
        NetworkModule.class
})
public interface ApplicationComponent {

    void inject(Mysplash application);
}
