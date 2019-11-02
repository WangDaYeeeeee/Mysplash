package com.wangdaye.common.ui.adapter.photo;

import android.content.Context;
import android.view.View;

import com.wangdaye.common.R;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.base.pager.ProfilePager;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.base.unsplash.User;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.common.presenter.DispatchCollectionsChangedPresenter;
import com.wangdaye.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.common.utils.FileUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.component.ComponentFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class PhotoItemEventHelper implements PhotoAdapter.ItemEventCallback {

    private MysplashActivity activity;
    private List<Photo> photoList;
    private LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    public PhotoItemEventHelper(MysplashActivity activity,
                                List<Photo> photoList,
                                LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter) {
        this.activity = activity;
        this.photoList = photoList;
        this.likeOrDislikePhotoPresenter = likeOrDislikePhotoPresenter;
    }

    @Override
    public void onStartPhotoActivity(View image, View background, int adapterPosition) {
        ArrayList<Photo> list = new ArrayList<>();
        int headIndex = adapterPosition - 2;
        int size = 5;
        if (headIndex < 0) {
            headIndex = 0;
        }
        if (headIndex + size - 1 > photoList.size() - 1) {
            size = photoList.size() - headIndex;
        }
        for (int i = headIndex; i < headIndex + size; i ++) {
            list.add(photoList.get(i));
        }

        ComponentFactory.getPhotoModule()
                .startPhotoActivity(activity, image, background, list, adapterPosition, headIndex);
    }

    @Override
    public void onStartUserActivity(View avatar, View background, User user, int index) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onDeleteButtonClicked(Photo photo, int adapterPosition) {
        // do nothing.
    }

    @Override
    public void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike) {
        if (AuthManager.getInstance().isAuthorized()) {
            try {
                Photo p1 = (Photo) photo.clone();
                Photo p2 = (Photo) photo.clone();
                p1.settingLike = true;
                p2.settingLike = true;
                MessageBus.getInstance().post(new PhotoEvent(p1));

                likeOrDislikePhotoPresenter.likeOrDislikePhoto(p2, setToLike);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        } else {
            ComponentFactory.getMeModule().startLoginActivity(activity);
        }
    }

    @Override
    public void onCollectButtonClicked(Photo photo, int adapterPosition) {
        if (!AuthManager.getInstance().isAuthorized()) {
            ComponentFactory.getMeModule().startLoginActivity(activity);
        } else {
            SelectCollectionDialog dialog = new SelectCollectionDialog();
            dialog.setPhotoAndListener(photo, new DispatchCollectionsChangedPresenter());
            dialog.show(activity.getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onDownloadButtonClicked(Photo photo, int adapterPosition) {
        if (isDownloading(activity, photo)) {
            NotificationHelper.showSnackbar(
                    activity, activity.getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isPhotoExists(activity, photo.id)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(photo);
            dialog.setOnCheckOrDownloadListener(new DownloadRepeatDialog.OnCheckOrDownloadListener() {
                @Override
                public void onCheck(Object obj) {
                    RoutingHelper.startCheckPhotoActivity(activity, photo.id);
                }

                @Override
                public void onDownload(Object obj) {
                    downloadPhoto(activity, photo);
                }
            });
            dialog.show(activity.getSupportFragmentManager(), null);
        } else {
            downloadPhoto(activity, photo);
        }
    }

    @Override
    public boolean isDownloading(Context context, Photo photo) {
        return ComponentFactory.getDownloaderService().isDownloading(context, photo.id);
    }

    public abstract void downloadPhoto(Context context, Photo photo);
}
