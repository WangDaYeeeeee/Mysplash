package com.wangdaye.search.base;

import android.content.Context;

import com.wangdaye.base.i.Downloadable;
import com.wangdaye.common.base.activity.ReadWriteActivity;
import com.wangdaye.base.DownloadTask;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.search.R;
import com.wangdaye.search.SearchActivity;

import java.util.List;

public class PhotoItemEventHelper extends com.wangdaye.common.ui.adapter.photo.PhotoItemEventHelper {

    private SearchActivity activity;

    public PhotoItemEventHelper(SearchActivity activity,
                                List<Photo> photoList,
                                LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter) {
        super(activity, photoList, likeOrDislikePhotoPresenter);
        this.activity = activity;
    }

    @Override
    public void downloadPhoto(Context context, Photo photo) {
        activity.requestReadWritePermission(photo, new ReadWriteActivity.RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                ComponentFactory.getDownloaderService().addTask(
                        activity,
                        (Photo) downloadable,
                        DownloadTask.DOWNLOAD_TYPE,
                        ComponentFactory.getSettingsService().getDownloadScale()
                );
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        activity, activity.getString(R.string.feedback_need_permission));
            }
        });
    }
}
