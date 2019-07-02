package com.wangdaye.mysplash.main.following.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.download.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.json.User;
import com.wangdaye.mysplash.common.ui.dialog.DownloadRepeatDialog;
import com.wangdaye.mysplash.common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash.common.utils.FileUtils;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.common.bus.event.PhotoEvent;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.presenter.DispatchCollectionsChangedPresenter;
import com.wangdaye.mysplash.common.presenter.list.LikeOrDislikePhotoPresenter;
import com.wangdaye.mysplash.user.ui.UserActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class FollowingItemEventHelper implements FollowingAdapter.ItemEventCallback {

    private MysplashActivity activity;
    private List<Photo> photoList;
    private LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter;

    public FollowingItemEventHelper(MysplashActivity activity,
                                    List<Photo> photoList,
                                    LikeOrDislikePhotoPresenter likeOrDislikePhotoPresenter) {
        this.activity = activity;
        this.photoList = photoList;
        this.likeOrDislikePhotoPresenter = likeOrDislikePhotoPresenter;
    }

    @Override
    public void onStartPhotoActivity(View image, View background,
                                     int adapterPosition, int photoPosition) {
        ArrayList<Photo> list = new ArrayList<>();
        int headIndex = photoPosition - 2;
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

        IntentHelper.startPhotoActivity(
                activity,
                image,
                background,
                list,
                photoPosition,
                headIndex
        );
    }

    @Override
    public void onStartUserActivity(View avatar, View background, User user, int index) {
        IntentHelper.startUserActivity(activity, avatar, background, user, UserActivity.PAGE_PHOTO);
    }

    @Override
    public void onVerbClicked(String verb, int adapterPosition) {
        if (!TextUtils.isEmpty(verb)) {
            IntentHelper.startSearchActivity(activity, verb);
        }
    }

    @Override
    public void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike) {
        if (AuthManager.getInstance().isAuthorized()) {
            photo.settingLike = true;
            MessageBus.getInstance().post(new PhotoEvent(photo));

            likeOrDislikePhotoPresenter.likeOrDislikePhoto(photo, setToLike);
        } else {
            IntentHelper.startLoginActivity(activity);
        }
    }

    @Override
    public void onCollectButtonClicked(Photo photo, int adapterPosition) {
        if (!AuthManager.getInstance().isAuthorized()) {
            IntentHelper.startLoginActivity(activity);
        } else {
            SelectCollectionDialog dialog = new SelectCollectionDialog();
            dialog.setPhotoAndListener(photo, new DispatchCollectionsChangedPresenter());
            dialog.show(activity.getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onDownloadButtonClicked(Photo photo, int adapterPosition) {
        if (DownloadHelper.getInstance(activity)
                .readDownloadingEntityCount(activity, photo.id) > 0) {
            NotificationHelper.showSnackbar(activity, activity.getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isPhotoExists(activity, photo.id)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(photo);
            dialog.setOnCheckOrDownloadListener(new DownloadRepeatDialog.OnCheckOrDownloadListener() {
                @Override
                public void onCheck(Object obj) {
                    IntentHelper.startCheckPhotoActivity(activity, photo.id);
                }

                @Override
                public void onDownload(Object obj) {
                    downloadPhoto(photo);
                }
            });
            dialog.show(activity.getSupportFragmentManager(), null);
        } else {
            downloadPhoto(photo);
        }
    }

    public abstract void downloadPhoto(Photo photo);
}
