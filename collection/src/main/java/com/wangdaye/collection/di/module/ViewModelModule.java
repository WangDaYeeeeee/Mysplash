package com.wangdaye.collection.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wangdaye.collection.vm.CollectionActivityModel;
import com.wangdaye.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.di.annotation.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(CollectionActivityModel.class)
    public abstract ViewModel getCollectionActivityModel(CollectionActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(CollectionPhotosViewModel.class)
    public abstract ViewModel getCollectionPhotosViewModel(CollectionPhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
