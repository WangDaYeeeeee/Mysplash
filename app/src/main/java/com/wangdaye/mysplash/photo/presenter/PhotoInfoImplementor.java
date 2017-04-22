package com.wangdaye.mysplash.photo.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoInfoService;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash.common.i.presenter.PhotoInfoPresenter;
import com.wangdaye.mysplash.common.i.view.PhotoInfoView;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Photo info implementor.
 * */

public class PhotoInfoImplementor
        implements PhotoInfoPresenter {

    private PhotoInfoModel model;
    private PhotoInfoView view;
    private OnRequestPhotoDetailsListener listener;

    public PhotoInfoImplementor(PhotoInfoModel model, PhotoInfoView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void requestPhoto(Context context) {
        listener = new OnRequestPhotoDetailsListener();
        // model.getPhotoInfoService().requestAPhoto(model.getPhoto().id, listener);
        model.getPhotoService().requestAPhoto(model.getPhoto().id, listener);
    }

    @Override
    public void cancelRequest() {
        if (listener != null) {
            listener.cancel();
        }
        // model.getPhotoInfoService().cancel();
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
    public PhotoInfoAdapter getAdapter() {
        return model.getAdapter();
    }

    @Override
    public boolean isFailed() {
        return model.isFailed();
    }

    @Override
    public void setFailed(boolean b) {
        model.setFailed(b);
    }

    // interface.

    private class OnRequestPhotoDetailsListener
            implements PhotoInfoService.OnRequestSinglePhotoListener {

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
                model.setPhoto(photo);
                model.setFailed(false);
                view.requestPhotoSuccess(photo);
            } else {
                view.requestPhotoFailed();
            }
        }

        @Override
        public void onRequestSinglePhotoFailed(Call<Photo> call, Throwable t) {
            if (canceled) {
                return;
            }
            view.requestPhotoFailed();
        }
    }
}
