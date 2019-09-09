package com.wangdaye.me.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.di.annotation.ViewModelKey;
import com.wangdaye.me.vm.MeActivityModel;
import com.wangdaye.me.vm.MeCollectionsViewModel;
import com.wangdaye.me.vm.MeLikesViewModel;
import com.wangdaye.me.vm.MePhotosViewModel;
import com.wangdaye.me.vm.MyFollowerViewModel;
import com.wangdaye.me.vm.MyFollowingViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(MeActivityModel.class)
    public abstract ViewModel getMeActivityModel(MeActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MeCollectionsViewModel.class)
    public abstract ViewModel getMeCollectionsViewModel(MeCollectionsViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MeLikesViewModel.class)
    public abstract ViewModel getMeLikesViewModel(MeLikesViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MePhotosViewModel.class)
    public abstract ViewModel getMePhotosViewModel(MePhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MyFollowerViewModel.class)
    public abstract ViewModel getMyFollowerViewModel(MyFollowerViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MyFollowingViewModel.class)
    public abstract ViewModel getMyFollowingViewModel(MyFollowingViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
