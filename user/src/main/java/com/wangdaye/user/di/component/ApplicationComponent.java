package com.wangdaye.user.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.user.UserActivity;
import com.wangdaye.user.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(UserActivity activity);
}
