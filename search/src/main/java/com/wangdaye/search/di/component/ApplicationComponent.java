package com.wangdaye.search.di.component;

import com.wangdaye.common.di.module.NetworkServiceModule;
import com.wangdaye.search.SearchActivity;
import com.wangdaye.search.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(SearchActivity activity);
}
