package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.utils.presenter.event.CollectionEventResponsePresenter;
import com.wangdaye.mysplash.common.utils.presenter.list.FollowOrCancelFollowPresenter;
import com.wangdaye.mysplash.common.utils.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.common.utils.presenter.event.PhotoEventResponsePresenter;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module(includes = {NetworkModule.class, ApplicationModule.class})
public class PresenterModule {

    @Provides
    public LikeOrDislikePhotoPresenter getLikeOrDislikePhotoPresenter(PhotoService service) {
        return new LikeOrDislikePhotoPresenter(service);
    }

    @Provides
    public FollowOrCancelFollowPresenter getFollowOrCancelFollowPresenter(FollowService service) {
        return new FollowOrCancelFollowPresenter(service);
    }

    @Provides
    public CollectionEventResponsePresenter getCollectionEventResponsePresenter(CompositeDisposable disposable) {
        return new CollectionEventResponsePresenter(disposable);
    }

    @Provides
    public PhotoEventResponsePresenter getPhotoEventResponsePresenter(CompositeDisposable disposable) {
        return new PhotoEventResponsePresenter(disposable);
    }
}
