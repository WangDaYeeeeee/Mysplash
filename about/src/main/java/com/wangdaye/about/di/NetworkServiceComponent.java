package com.wangdaye.about.di;

import com.wangdaye.about.ui.TotalDialog;
import com.wangdaye.common.di.module.NetworkServiceModule;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(TotalDialog dialog);
}
