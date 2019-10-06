package com.wangdaye.downloader.di;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.downloader.DownloaderServiceIMP;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(DownloaderServiceIMP helper);
}
