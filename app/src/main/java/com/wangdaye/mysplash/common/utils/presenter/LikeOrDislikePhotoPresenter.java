package com.wangdaye.mysplash.common.utils.presenter;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.network.callback.Callback;
import com.wangdaye.mysplash.common.network.json.LikePhotoResult;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.network.service.PhotoService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.main.following.ui.FollowingAdapter;

import javax.inject.Inject;

import androidx.recyclerview.widget.RecyclerView;

public class LikeOrDislikePhotoPresenter {

    private PhotoService service;

    @Inject
    public LikeOrDislikePhotoPresenter(PhotoService service) {
        this.service = service;
    }

    public void likeOrDislikePhoto(RecyclerView recyclerView, PhotoAdapter adapter,
                                   Photo photo, boolean setToLike) {
        Callback<LikePhotoResult> callback = new Callback<LikePhotoResult>() {
            @Override
            public void onSucceed(LikePhotoResult likePhotoResult) {
                photo.settingLike = false;
                photo.liked_by_user = likePhotoResult.photo.liked_by_user;
                photo.likes = likePhotoResult.photo.likes;
                Mysplash.getInstance().dispatchPhotoUpdate(photo, Mysplash.MessageType.UPDATE);

                User user = AuthManager.getInstance().getUser();
                if (user != null) {
                    user.total_likes += setToLike ? 1 : -1;
                    Mysplash.getInstance().dispatchUserUpdate(user, Mysplash.MessageType.UPDATE);
                }
            }

            @Override
            public void onFailed() {
                photo.settingLike = false;
                adapter.updatePhoto(recyclerView, photo, true, true);
            }
        };

        if (setToLike) {
            service.likePhoto(photo.id, callback);
        } else {
            service.cancelLikePhoto(photo.id, callback);
        }
    }

    public void likeOrDislikePhoto(RecyclerView recyclerView, FollowingAdapter adapter,
                                   Photo photo, boolean setToLike) {
        Callback<LikePhotoResult> callback = new Callback<LikePhotoResult>() {
            @Override
            public void onSucceed(LikePhotoResult likePhotoResult) {
                photo.settingLike = false;
                photo.liked_by_user = likePhotoResult.photo.liked_by_user;
                photo.likes = likePhotoResult.photo.likes;
                Mysplash.getInstance().dispatchPhotoUpdate(photo, Mysplash.MessageType.UPDATE);

                User user = AuthManager.getInstance().getUser();
                if (user != null) {
                    user.total_likes += setToLike ? 1 : -1;
                    Mysplash.getInstance().dispatchUserUpdate(user, Mysplash.MessageType.UPDATE);
                }
            }

            @Override
            public void onFailed() {
                photo.settingLike = false;
                adapter.updatePhoto(recyclerView, photo, true, true);
            }
        };

        if (setToLike) {
            service.likePhoto(photo.id, callback);
        } else {
            service.cancelLikePhoto(photo.id, callback);
        }
    }
}
