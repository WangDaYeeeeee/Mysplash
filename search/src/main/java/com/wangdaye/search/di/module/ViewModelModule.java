package com.wangdaye.search.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.di.annotation.ViewModelKey;
import com.wangdaye.search.vm.CollectionSearchPageViewModel;
import com.wangdaye.search.vm.PhotoSearchPageViewModel;
import com.wangdaye.search.vm.SearchActivityModel;
import com.wangdaye.search.vm.UserSearchPageViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(CollectionSearchPageViewModel.class)
    public abstract ViewModel getCollectionSearchPageViewModel(CollectionSearchPageViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PhotoSearchPageViewModel.class)
    public abstract ViewModel getPhotoSearchPageViewModel(PhotoSearchPageViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(SearchActivityModel.class)
    public abstract ViewModel getSearchActivityModel(SearchActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserSearchPageViewModel.class)
    public abstract ViewModel getUserSearchPageViewModel(UserSearchPageViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
