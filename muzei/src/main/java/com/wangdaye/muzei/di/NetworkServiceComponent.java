package com.wangdaye.muzei.di;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.muzei.provider.MysplashMuzeiWorker;
import com.wangdaye.muzei.service.MysplashMuzeiArtSource;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(MysplashMuzeiWorker helper);

    void inject(MysplashMuzeiArtSource source);
}
