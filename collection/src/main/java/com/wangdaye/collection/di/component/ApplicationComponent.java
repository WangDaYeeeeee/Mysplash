package com.wangdaye.collection.di.component;

import com.wangdaye.collection.CollectionActivity;
import com.wangdaye.collection.di.module.ViewModelModule;
import com.wangdaye.collection.ui.UpdateCollectionDialog;
import com.wangdaye.common.di.module.NetworkServiceModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(CollectionActivity activity);

    void inject(UpdateCollectionDialog dialog);
}
