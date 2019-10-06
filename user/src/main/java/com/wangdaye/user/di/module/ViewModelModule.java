package com.wangdaye.user.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.di.annotation.ViewModelKey;
import com.wangdaye.user.vm.UserActivityModel;
import com.wangdaye.user.vm.UserCollectionsViewModel;
import com.wangdaye.user.vm.UserLikesViewModel;
import com.wangdaye.user.vm.UserPhotosViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(UserActivityModel.class)
    public abstract ViewModel getUserActivityModel(UserActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserCollectionsViewModel.class)
    public abstract ViewModel getUserCollectionsViewModel(UserCollectionsViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserLikesViewModel.class)
    public abstract ViewModel getUserLikesViewModel(UserLikesViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserPhotosViewModel.class)
    public abstract ViewModel getUserPhotosViewModel(UserPhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
