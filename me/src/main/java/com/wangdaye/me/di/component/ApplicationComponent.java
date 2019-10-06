package com.wangdaye.me.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.me.activity.LoginActivity;
import com.wangdaye.me.activity.MeActivity;
import com.wangdaye.me.activity.MyFollowActivity;
import com.wangdaye.me.activity.UpdateMeActivity;
import com.wangdaye.me.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(MeActivity activity);
    void inject(MyFollowActivity activity);
    void inject(LoginActivity activity);
    void inject(UpdateMeActivity activity);
}
