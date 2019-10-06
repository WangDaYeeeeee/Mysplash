package com.wangdaye.common.di.component;

import com.wangdaye.common.di.module.NetworkModule;
import com.wangdaye.common.network.ComponentCollection;

import dagger.Component;

@Component(modules = NetworkModule.class)
public interface NetworkComponent {

    void inject(ComponentCollection collection);
}
