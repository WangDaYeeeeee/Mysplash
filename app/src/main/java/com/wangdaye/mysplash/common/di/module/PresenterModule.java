package com.wangdaye.mysplash.common.di.module;

import com.wangdaye.mysplash.common.network.service.FollowService;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.utils.presenter.BrowsableDialogMangePresenter;
import com.wangdaye.mysplash.common.utils.presenter.FollowOrCancelFollowPresenter;
import com.wangdaye.mysplash.common.utils.presenter.LikeOrDislikePhotoPresenter;

import dagger.Module;
import dagger.Provides;

@Module(includes = NetworkModule.class)
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
    public BrowsableDialogMangePresenter getBrowsableDialogMangePresenter() {
        return new BrowsableDialogMangePresenter();
    }
}
