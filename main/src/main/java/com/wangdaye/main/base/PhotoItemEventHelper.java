package com.wangdaye.main.base;

import android.content.Context;

import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.main.MainActivity;

import java.util.List;

public class PhotoItemEventHelper extends com.wangdaye.common.ui.adapter.photo.PhotoItemEventHelper {

    private MainActivity activity;

    public PhotoItemEventHelper(MainActivity activity,
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
