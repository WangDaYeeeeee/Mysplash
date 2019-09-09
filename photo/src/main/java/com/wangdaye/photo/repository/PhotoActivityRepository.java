package com.wangdaye.photo.repository;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.LikePhotoResult;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.PhotoService;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.bus.MessageBus;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class PhotoActivityRepository {

    private PhotoService photoService;
    private PhotoService likeService;

    @Inject
    public PhotoActivityRepository(PhotoService photoService, PhotoService likeService) {
        this.photoService = photoService;
        this.likeService = likeService;
    }

    public void getAPhoto(@NonNull MutableLiveData<Resource<Photo>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        photoService.cancel();
        photoService.requestAPhoto(id, new BaseObserver<Photo>() {
            @Override
            public void onSucceed(Photo photo) {
                if (current.getValue() == null || current.getValue().data == null) {
                    photo.complete = true;
                    current.setValue(Resource.success(photo));
                } else if (current.getValue().data.id.equals(id)) {
                    photo.complete = true;
                    photo.settingLike = current.getValue().data.settingLike;
                    current.setValue(Resource.success(photo));
                }
            }

            @Override
            public void onFailed() {
                if (current.getValue() == null || current.getValue().data == null) {
                    current.setValue(Resource.error(null));
                } else if (current.getValue().data.id.equals(id)) {
                    current.setValue(Resource.error(current.getValue().data));
                }
            }
        });
    }

    public void likeOrDislikePhoto(@NonNull MutableLiveData<Resource<Photo>> current,
                                   String id, boolean setToLike) {
        if (current.getValue() != null
                && current.getValue().data != null) {
            Photo photo = current.getValue().data;
            photo.settingLike = true;
            current.setValue(Resource.loading(photo));

            likeService.cancel();
            if (setToLike) {
                likeService.likePhoto(id, new SetLikeCallback(current, id));
            } else {
                likeService.cancelLikePhoto(id, new SetLikeCallback(current, id));
            }
        }
    }

    public void cancel() {
        photoService.cancel();
        likeService.cancel();
    }

    // interface.

    private class SetLikeCallback extends BaseObserver<LikePhotoResult> {

        private MutableLiveData<Resource<Photo>> current;
        private String photoId;

        SetLikeCallback(MutableLiveData<Resource<Photo>> current, String photoId) {
            this.current = current;
            this.photoId = photoId;
        }

        @Override
        public void onSucceed(LikePhotoResult likePhotoResult) {
            if (current.getValue() != null
                    && current.getValue().data != null
                    && current.getValue().data.id.equals(photoId)) {
                Photo photo = current.getValue().data;
                photo.liked_by_user = likePhotoResult.photo.liked_by_user;
                photo.likes = likePhotoResult.photo.likes;
                photo.settingLike = false;
                MessageBus.getInstance().post(new PhotoEvent(photo));

                User user = AuthManager.getInstance().getUser();
                if (user != null) {
                    user.total_likes = likePhotoResult.user.total_likes;
                    MessageBus.getInstance().post(user);
                }
            }
        }

        @Override
        public void onFailed() {
            if (current.getValue() != null
                    && current.getValue().data != null
                    && current.getValue().data.id.equals(photoId)) {
                Photo photo = current.getValue().data;
                photo.settingLike = false;
                MessageBus.getInstance().post(new PhotoEvent(photo));
            }
        }
    }
}
