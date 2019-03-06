package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.collection.repository.CollectionActivityRepository;
import com.wangdaye.mysplash.collection.repository.CollectionPhotosViewRepository;
import com.wangdaye.mysplash.common.network.service.CollectionService;
import com.wangdaye.mysplash.common.network.service.FeedService;
import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.network.service.SearchService;
import com.wangdaye.mysplash.common.network.service.UserService;
import com.wangdaye.mysplash.main.collection.CollectionsViewRepository;
import com.wangdaye.mysplash.main.following.FollowingFeedViewRepository;
import com.wangdaye.mysplash.main.home.HomePhotosViewRepository;
import com.wangdaye.mysplash.main.multiFilter.MultiFilterPhotoViewRepository;
import com.wangdaye.mysplash.main.selected.SelectedViewRepository;
import com.wangdaye.mysplash.me.repository.MeCollectionsViewRepository;
import com.wangdaye.mysplash.me.repository.MePhotosViewRepository;
import com.wangdaye.mysplash.me.repository.MyFollowUserViewRepository;
import com.wangdaye.mysplash.photo3.PhotoActivityRepository;
import com.wangdaye.mysplash.search.repository.CollectionSearchPageViewRepository;
import com.wangdaye.mysplash.search.repository.PhotoSearchPageViewRepository;
import com.wangdaye.mysplash.search.repository.UserSearchPageViewRepository;
import com.wangdaye.mysplash.user.repository.UserActivityRepository;
import com.wangdaye.mysplash.user.repository.UserCollectionsViewRepository;
import com.wangdaye.mysplash.user.repository.UserPhotosViewRepository;

import dagger.Module;
import dagger.Provides;

@Module(includes = NetworkModule.class)
public class RepositoryModule {

    @Provides
    public CollectionActivityRepository getCollectionBrowsableRepository(CollectionService service) {
        return new CollectionActivityRepository(service);
    }

    @Provides
    public CollectionPhotosViewRepository getCollectionPhotosViewRepository(PhotoService service) {
        return new CollectionPhotosViewRepository(service);
    }

    @Provides
    public CollectionsViewRepository getCollectionsViewRepository(CollectionService service) {
        return new CollectionsViewRepository(service);
    }

    @Provides
    public FollowingFeedViewRepository getFollowingFeedViewRepository(FeedService service) {
        return new FollowingFeedViewRepository(service);
    }

    @Provides
    public HomePhotosViewRepository getHomePhotosViewRepository(PhotoService service) {
        return new HomePhotosViewRepository(service);
    }

    @Provides
    public MultiFilterPhotoViewRepository getMultiFilterPhotoViewRepository(PhotoService service) {
        return new MultiFilterPhotoViewRepository(service);
    }

    @Provides
    public SelectedViewRepository getSelectedViewRepository(CollectionService service) {
        return new SelectedViewRepository(service);
    }

    @Provides
    public MeCollectionsViewRepository getMeCollectionsViewRepository(CollectionService service) {
        return new MeCollectionsViewRepository(service);
    }

    @Provides
    public MePhotosViewRepository getMePhotosViewRepository(PhotoService service) {
        return new MePhotosViewRepository(service);
    }

    @Provides
    public MyFollowUserViewRepository getMyFollowUserViewRepository(UserService service) {
        return new MyFollowUserViewRepository(service);
    }

    @Provides
    public PhotoActivityRepository getPhotoActivityRepository(PhotoService photoService,
                                                              PhotoService likeService) {
        return new PhotoActivityRepository(photoService, likeService);
    }

    @Provides
    public CollectionSearchPageViewRepository getCollectionSearchPageViewRepository(SearchService service) {
        return new CollectionSearchPageViewRepository(service);
    }

    @Provides
    public PhotoSearchPageViewRepository getPhotoSearchPageViewRepository(SearchService service) {
        return new PhotoSearchPageViewRepository(service);
    }

    @Provides
    public UserSearchPageViewRepository getUserSearchPageViewRepository(SearchService service) {
        return new UserSearchPageViewRepository(service);
    }

    @Provides
    public UserActivityRepository getUserBrowsableViewRepository(UserService userService,
                                                                 FollowService followService) {
        return new UserActivityRepository(userService, followService);
    }

    @Provides
    public UserCollectionsViewRepository getUserCollectionsViewRepository(CollectionService service) {
        return new UserCollectionsViewRepository(service);
    }

    @Provides
    public UserPhotosViewRepository getUserPhotosViewRepository(PhotoService service) {
        return new UserPhotosViewRepository(service);
    }
}
