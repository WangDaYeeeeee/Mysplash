package com.wangdaye.photo.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.photo.activity.PhotoActivity;
import com.wangdaye.photo.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(PhotoActivity activity);
}
