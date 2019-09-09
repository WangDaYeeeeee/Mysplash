package com.wangdaye.main.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wangdaye.common.base.vm.PagerManageViewModel;
import com.wangdaye.common.base.vm.ParamsViewModelFactory;
import com.wangdaye.common.di.annotation.ViewModelKey;
import com.wangdaye.main.MainActivityModel;
import com.wangdaye.main.collection.vm.AllCollectionsViewModel;
import com.wangdaye.main.collection.vm.CuratedCollectionsViewModel;
import com.wangdaye.main.collection.vm.FeaturedCollectionsViewModel;
import com.wangdaye.main.following.FollowingFeedViewModel;
import com.wangdaye.main.home.vm.FeaturedHomePhotosViewModel;
import com.wangdaye.main.home.vm.NewHomePhotosViewModel;
import com.wangdaye.main.home.vm.SearchBarViewModel;
import com.wangdaye.main.multiFilter.vm.MultiFilterFragmentModel;
import com.wangdaye.main.multiFilter.vm.MultiFilterPhotoViewModel;
import com.wangdaye.main.selected.SelectedViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(AllCollectionsViewModel.class)
    public abstract ViewModel getAllCollectionsViewModel(AllCollectionsViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(CuratedCollectionsViewModel.class)
    public abstract ViewModel getCuratedCollectionsViewModel(CuratedCollectionsViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedCollectionsViewModel.class)
    public abstract ViewModel getFeaturedCollectionsViewModel(FeaturedCollectionsViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(FollowingFeedViewModel.class)
    public abstract ViewModel getFollowingFeedViewModel(FollowingFeedViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(SearchBarViewModel.class)
    public abstract ViewModel getSearchBarViewModel(SearchBarViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(FeaturedHomePhotosViewModel.class)
    public abstract ViewModel getFeaturedHomePhotosViewModel(FeaturedHomePhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(NewHomePhotosViewModel.class)
    public abstract ViewModel getNewHomePhotosViewModel(NewHomePhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MultiFilterFragmentModel.class)
    public abstract ViewModel getMultiFilterFragmentModel(MultiFilterFragmentModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MultiFilterPhotoViewModel.class)
    public abstract ViewModel getMultiFilterPhotoViewModel(MultiFilterPhotoViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(SelectedViewModel.class)
    public abstract ViewModel getSelectedViewModel(SelectedViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityModel.class)
    public abstract ViewModel getMainActivityModel(MainActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
