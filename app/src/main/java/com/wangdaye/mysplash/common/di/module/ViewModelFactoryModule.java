package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.collection.vm.CollectionActivityModel;
import com.wangdaye.mysplash.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.mysplash.common.basic.vm.PagerManageViewModel;
import com.wangdaye.mysplash.common.basic.DaggerViewModelFactory;
import com.wangdaye.mysplash.common.di.annotation.ViewModelKey;
import com.wangdaye.mysplash.main.MainActivityModel;
import com.wangdaye.mysplash.main.collection.vm.AllCollectionsViewModel;
import com.wangdaye.mysplash.main.collection.vm.CuratedCollectionsViewModel;
import com.wangdaye.mysplash.main.collection.vm.FeaturedCollectionsViewModel;
import com.wangdaye.mysplash.main.following.FollowingFeedViewModel;
import com.wangdaye.mysplash.main.home.vm.FeaturedHomePhotosViewModel;
import com.wangdaye.mysplash.main.home.vm.NewHomePhotosViewModel;
import com.wangdaye.mysplash.main.multiFilter.vm.MultiFilterFragmentModel;
import com.wangdaye.mysplash.main.multiFilter.vm.MultiFilterPhotoViewModel;
import com.wangdaye.mysplash.main.selected.SelectedViewModel;
import com.wangdaye.mysplash.me.vm.MeActivityModel;
import com.wangdaye.mysplash.me.vm.MeCollectionsViewModel;
import com.wangdaye.mysplash.me.vm.MeLikesViewModel;
import com.wangdaye.mysplash.me.vm.MePhotosViewModel;
import com.wangdaye.mysplash.me.vm.MyFollowerViewModel;
import com.wangdaye.mysplash.me.vm.MyFollowingViewModel;
import com.wangdaye.mysplash.photo3.PhotoActivityModel;
import com.wangdaye.mysplash.search.vm.CollectionSearchPageViewModel;
import com.wangdaye.mysplash.search.vm.PhotoSearchPageViewModel;
import com.wangdaye.mysplash.search.vm.SearchActivityModel;
import com.wangdaye.mysplash.search.vm.UserSearchPageViewModel;
import com.wangdaye.mysplash.user.vm.UserActivityModel;
import com.wangdaye.mysplash.user.vm.UserCollectionsViewModel;
import com.wangdaye.mysplash.user.vm.UserLikesViewModel;
import com.wangdaye.mysplash.user.vm.UserPhotosViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(DaggerViewModelFactory factory);

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
    @ViewModelKey(PhotoActivityModel.class)
    public abstract ViewModel getPhotoActivityModel(PhotoActivityModel model);

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
