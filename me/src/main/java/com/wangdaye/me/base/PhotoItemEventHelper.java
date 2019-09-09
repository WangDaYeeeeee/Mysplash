package com.wangdaye.me.base;

import android.content.Context;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.me.activity.MeActivity;

import java.util.List;

public class PhotoItemEventHelper extends com.wangdaye.common.ui.adapter.photo.PhotoItemEventHelper {

    private MeActivity activity;

    public PhotoItemEventHelper(MeActivity activity,
                                List<Photo> photoList,
                                LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter) {
        super(activity, photoList, likeOrDislikePhotoPresenter);
        this.activity = activity;
    }

    @Override
    public void downloadPhoto(Context context, Photo photo) {
        activity.downloadPhoto(photo);
    }
}
