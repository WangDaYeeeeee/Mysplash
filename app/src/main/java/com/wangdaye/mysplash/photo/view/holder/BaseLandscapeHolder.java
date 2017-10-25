package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.PhotoButtonBar;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Base landscape holder.
 *
 * This view holder is used to show the basic part of the photo information when device is
 * horizontal.
 *
 * */

public class BaseLandscapeHolder extends PhotoInfoAdapter.ViewHolder
        implements PhotoButtonBar.OnClickButtonListener {

    private PhotoActivity activity;

    @BindView(R.id.item_photo_base_landscape_btnBar)
    PhotoButtonBar buttonBar;

    public static final int TYPE_BASE_LANDSCAPE = 9;

    public BaseLandscapeHolder(PhotoActivity a, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.activity = a;
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        buttonBar.setState(photo);
        if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(photo.id) > 0) {
            a.startCheckDownloadProgressThread();
        }
        buttonBar.setOnClickButtonListener(this);
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    public PhotoButtonBar getButtonBar() {
        return buttonBar;
    }

    // interface.

    // on click button listener.

    @Override
    public void onLikeButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            activity.likePhoto();
        } else {
            IntentHelper.startLoginActivity(activity);
        }
    }

    @Override
    public void onCollectButtonClicked() {
        if (AuthManager.getInstance().isAuthorized()) {
            activity.collectPhoto();
        } else {
            IntentHelper.startLoginActivity(activity);
        }
    }

    @Override
    public void onDownloadButtonClicked() {
        activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE, true);
    }

    @Override
    public void onDownloadButtonLongClicked() {
        activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE);
    }
}
