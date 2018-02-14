package com.wangdaye.mysplash.photo2.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common.data.entity.unsplash.LikePhotoResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel2;
import com.wangdaye.mysplash.common.i.presenter.PhotoInfoPresenter2;
import com.wangdaye.mysplash.common.i.view.PhotoInfoView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Photo info implementor.
 * */

public class PhotoInfoImplementor
        implements PhotoInfoPresenter2 {

    private PhotoInfoModel2 model;
    private PhotoInfoView view;

    private OnRequestPhotoDetailsListener requestPhotoListener;

    public PhotoInfoImplementor(PhotoInfoModel2 model, PhotoInfoView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestPhoto(Context context) {
        requestPhotoListener = new OnRequestPhotoDetailsListener();
        model.getPhotoService().requestAPhoto(model.getPhoto().id, requestPhotoListener);
    }

    @Override
    public void setLikeForAPhoto(Context context) {
        Photo photo = model.getPhoto();
        photo.settingLike = true;
        model.setPhoto(photo, false);
        model.getPhotoService().setLikeForAPhoto(
                model.getPhoto().id,
                !model.getPhoto().liked_by_user,
                new OnSetLikeForAPhotoListener(photo.id));
    }

    @Override
    public void cancelRequest() {
        if (requestPhotoListener != null) {
            requestPhotoListener.cancel();
        }
        model.getPhotoService().cancel();
    }

    @Override
    public void touchMenuItem(int itemId) {
        view.touchMenuItem(itemId);
    }

    @Override
    public Photo getPhoto() {
        return model.getPhoto();
    }

    @Override
    public void setPhoto(Photo photo, boolean init) {
        model.setPhoto(photo, init);
    }

    @Override
    public PhotoInfoAdapter2 getAdapter() {
        return model.getAdapter();
    }

    @Override
    public boolean isFailed() {
        return model.isFailed();
    }

    // interface.

    // on request single photo requestPhotoListener.

    private class OnRequestPhotoDetailsListener
            implements PhotoService.OnRequestSinglePhotoListener {

        private boolean canceled;

        OnRequestPhotoDetailsListener() {
            this.canceled = false;
        }

        public void cancel() {
            this.canceled = true;
        }

        @Override
        public void onRequestSinglePhotoSuccess(Call<Photo> call, Response<Photo> response) {
            if (canceled) {
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                Photo photo = response.body();
                photo.complete = true;
                photo.settingLike = model.getPhoto().settingLike;
                model.setPhoto(photo, false);
                model.setFailed(false);
                view.requestPhotoSuccess(photo);
            } else {
                model.setFailed(true);
                view.requestPhotoFailed();
            }
        }

        @Override
        public void onRequestSinglePhotoFailed(Call<Photo> call, Throwable t) {
            if (canceled) {
                return;
            }
            model.setFailed(true);
            view.requestPhotoFailed();
        }
    }

    // on set like requestPhotoListener.

    private class OnSetLikeForAPhotoListener implements PhotoService.OnSetLikeListener {

        private String id;

        OnSetLikeForAPhotoListener(String id) {
            this.id = id;
        }

        @Override
        public void onSetLikeSuccess(Call<LikePhotoResult> call, Response<LikePhotoResult> response) {
            if (!model.getPhoto().id.equals(id)) {
                return;
            }
            Photo photo = model.getPhoto();
            photo.settingLike = false;
            if (response.isSuccessful() && response.body() != null) {
                photo.liked_by_user = response.body().photo.liked_by_user;
            }
            model.setPhoto(photo, false);
            view.setLikeForAPhotoCompleted(photo, true);
        }

        @Override
        public void onSetLikeFailed(Call<LikePhotoResult> call, Throwable t) {
            if (!model.getPhoto().id.equals(id)) {
                return;
            }
            Photo photo = model.getPhoto();
            photo.settingLike = false;
            model.setPhoto(photo, false);
            view.setLikeForAPhotoCompleted(photo, false);
        }
    }
}
