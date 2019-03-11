package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.collection.vm.CollectionActivityModel;
import com.wangdaye.mysplash.collection.vm.CollectionPhotosViewModel;
import com.wangdaye.mysplash.collection.repository.CollectionActivityRepository;
import com.wangdaye.mysplash.collection.repository.CollectionPhotosViewRepository;
import com.wangdaye.mysplash.common.basic.vm.PagerManageViewModel;
import com.wangdaye.mysplash.common.utils.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.common.utils.presenter.event.UserEventResponsePresenter;
import com.wangdaye.mysplash.main.MainActivityModel;
import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;
import com.wangdaye.mysplash.main.collection.vm.AllCollectionsViewModel;
import com.wangdaye.mysplash.main.collection.vm.CuratedCollectionsViewModel;
import com.wangdaye.mysplash.main.collection.vm.FeaturedCollectionsViewModel;
import com.wangdaye.mysplash.main.following.FollowingFeedViewModel;
import com.wangdaye.mysplash.main.following.FollowingFeedViewRepository;
import com.wangdaye.mysplash.main.home.HomePhotosViewRepository;
import com.wangdaye.mysplash.main.home.vm.FeaturedHomePhotosViewModel;
import com.wangdaye.mysplash.main.home.vm.NewHomePhotosViewModel;
import com.wangdaye.mysplash.main.multiFilter.MultiFilterPhotoViewRepository;
import com.wangdaye.mysplash.main.multiFilter.vm.MultiFilterFragmentModel;
import com.wangdaye.mysplash.main.multiFilter.vm.MultiFilterPhotoViewModel;
import com.wangdaye.mysplash.main.selected.SelectedViewModel;
import com.wangdaye.mysplash.main.selected.SelectedViewRepository;
import com.wangdaye.mysplash.me.vm.MeActivityModel;
import com.wangdaye.mysplash.me.vm.MeCollectionsViewModel;
import com.wangdaye.mysplash.me.vm.MeLikesViewModel;
import com.wangdaye.mysplash.me.vm.MePhotosViewModel;
import com.wangdaye.mysplash.me.vm.MyFollowerViewModel;
import com.wangdaye.mysplash.me.vm.MyFollowingViewModel;
import com.wangdaye.mysplash.me.repository.MeCollectionsViewRepository;
import com.wangdaye.mysplash.me.repository.MePhotosViewRepository;
import com.wangdaye.mysplash.me.repository.MyFollowUserViewRepository;
import com.wangdaye.mysplash.photo3.PhotoActivityModel;
import com.wangdaye.mysplash.photo3.PhotoActivityRepository;
import com.wangdaye.mysplash.search.vm.CollectionSearchPageViewModel;
import com.wangdaye.mysplash.search.vm.PhotoSearchPageViewModel;
import com.wangdaye.mysplash.search.vm.SearchActivityModel;
import com.wangdaye.mysplash.search.vm.UserSearchPageViewModel;
import com.wangdaye.mysplash.search.repository.CollectionSearchPageViewRepository;
import com.wangdaye.mysplash.search.repository.PhotoSearchPageViewRepository;
import com.wangdaye.mysplash.search.repository.UserSearchPageViewRepository;
import com.wangdaye.mysplash.user.vm.UserActivityModel;
import com.wangdaye.mysplash.user.vm.UserCollectionsViewModel;
import com.wangdaye.mysplash.user.vm.UserLikesViewModel;
import com.wangdaye.mysplash.user.vm.UserPhotosViewModel;
import com.wangdaye.mysplash.user.repository.UserActivityRepository;
import com.wangdaye.mysplash.user.repository.UserCollectionsViewRepository;
import com.wangdaye.mysplash.user.repository.UserPhotosViewRepository;

import dagger.Module;
import dagger.Provides;

@Module(includes = {RepositoryModule.class, PresenterModule.class})
public class ViewModelModule {
    
    @Provides
    public CollectionActivityModel getCollectionActivityModel(CollectionActivityRepository repository) {
        return new CollectionActivityModel(repository);
    }

    @Provides
    public CollectionPhotosViewModel getCollectionPhotosViewModel(CollectionPhotosViewRepository repository,
                                                                  PhotoEventResponsePresenter presenter) {
        return new CollectionPhotosViewModel(repository, presenter);
    }

    @Provides
    public AllCollectionsViewModel getAllCollectionsViewModel(CollectionsViewRepository repository,
                                                              CollectionEventResponsePresenter presenter) {
        return new AllCollectionsViewModel(repository, presenter);
    }

    @Provides
    public CuratedCollectionsViewModel getCuratedCollectionsViewModel(CollectionsViewRepository repository,
                                                                      CollectionEventResponsePresenter presenter) {
        return new CuratedCollectionsViewModel(repository, presenter);
    }

    @Provides
    public FeaturedCollectionsViewModel getFeaturedCollectionsViewModel(CollectionsViewRepository repository,
                                                                        CollectionEventResponsePresenter presenter) {
        return new FeaturedCollectionsViewModel(repository, presenter);
    }

    @Provides
    public FollowingFeedViewModel getFollowingFeedViewModel(FollowingFeedViewRepository repository,
                                                            PhotoEventResponsePresenter presenter) {
        return new FollowingFeedViewModel(repository, presenter);
    }

    @Provides
    public FeaturedHomePhotosViewModel getFeaturedHomePhotosViewModel(HomePhotosViewRepository repository,
                                                                      PhotoEventResponsePresenter presenter) {
        return new FeaturedHomePhotosViewModel(repository, presenter);
    }

    @Provides
    public NewHomePhotosViewModel getNewHomePhotosViewModel(HomePhotosViewRepository repository,
                                                            PhotoEventResponsePresenter presenter) {
        return new NewHomePhotosViewModel(repository, presenter);
    }

    @Provides
    public MultiFilterFragmentModel getMultiFilterFragmentModel() {
        return new MultiFilterFragmentModel();
    }

    @Provides
    public MultiFilterPhotoViewModel getMultiFilterPhotoViewModel(MultiFilterPhotoViewRepository repository,
                                                                  PhotoEventResponsePresenter presenter) {
        return new MultiFilterPhotoViewModel(repository, presenter);
    }

    @Provides
    public SelectedViewModel getSelectedViewModel(SelectedViewRepository repository) {
        return new SelectedViewModel(repository);
    }

    @Provides
    public MainActivityModel getMainActivityModel() {
        return new MainActivityModel();
    }

    @Provides
    public MeActivityModel getMeActivityModel() {
        return new MeActivityModel();
    }

    @Provides
    public MeCollectionsViewModel getMeCollectionsViewModel(MeCollectionsViewRepository repository,
                                                            CollectionEventResponsePresenter presenter) {
        return new MeCollectionsViewModel(repository, presenter);
    }

    @Provides
    public MeLikesViewModel getMeLikesViewModel(MePhotosViewRepository repository,
                                                PhotoEventResponsePresenter presenter) {
        return new MeLikesViewModel(repository, presenter);
    }

    @Provides
    public MePhotosViewModel getMePhotosViewModel(MePhotosViewRepository repository,
                                                  PhotoEventResponsePresenter presenter) {
        return new MePhotosViewModel(repository, presenter);
    }

    @Provides
    public MyFollowerViewModel getMyFollowerViewModel(MyFollowUserViewRepository repository,
                                                      UserEventResponsePresenter presenter) {
        return new MyFollowerViewModel(repository, presenter);
    }

    @Provides
    public MyFollowingViewModel getMyFollowingViewModel(MyFollowUserViewRepository repository,
                                                        UserEventResponsePresenter presenter) {
        return new MyFollowingViewModel(repository, presenter);
    }

    @Provides
    public PhotoActivityModel getPhotoActivityModel(PhotoActivityRepository repository) {
        return new PhotoActivityModel(repository);
    }

    @Provides
    public CollectionSearchPageViewModel getCollectionSearchPageViewModel(CollectionSearchPageViewRepository repository,
                                                                          CollectionEventResponsePresenter presenter) {
        return new CollectionSearchPageViewModel(repository, presenter);
    }

    @Provides
    public PhotoSearchPageViewModel getPhotoSearchPageViewModel(PhotoSearchPageViewRepository repository,
                                                                PhotoEventResponsePresenter presenter) {
        return new PhotoSearchPageViewModel(repository, presenter);
    }

    @Provides
    public SearchActivityModel getSearchActivityModel() {
        return new SearchActivityModel();
    }

    @Provides
    public UserSearchPageViewModel getUserSearchPageViewModel(UserSearchPageViewRepository repository,
                                                              UserEventResponsePresenter presenter) {
        return new UserSearchPageViewModel(repository, presenter);
    }

    @Provides
    public UserActivityModel getUserActivityModel(UserActivityRepository repository) {
        return new UserActivityModel(repository);
    }

    @Provides
    public UserCollectionsViewModel getUserCollectionsViewModel(UserCollectionsViewRepository repository,
                                                                CollectionEventResponsePresenter presenter) {
        return new UserCollectionsViewModel(repository, presenter);
    }

    @Provides
    public UserLikesViewModel getUserLikesViewModel(UserPhotosViewRepository repository,
                                                    PhotoEventResponsePresenter presenter) {
        return new UserLikesViewModel(repository, presenter);
    }

    @Provides
    public UserPhotosViewModel getUserPhotosViewModel(UserPhotosViewRepository repository,
                                                      PhotoEventResponsePresenter presenter) {
        return new UserPhotosViewModel(repository, presenter);
    }

    @Provides
    public PagerManageViewModel getPagerManageViewModel() {
        return new PagerManageViewModel();
    }
}
