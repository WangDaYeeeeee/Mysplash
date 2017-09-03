package com.wangdaye.mysplash.photo.view.holder;

import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.common.ui.widget.PhotoDownloadView;
import com.wangdaye.mysplash.common.utils.helper.DatabaseHelper;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Base landscape holder.
 *
 * This view holder is used to show the basic part of the photo information when device is
 * horizontal.
 *
 * */

public class BaseLandscapeHolder extends PhotoInfoAdapter.ViewHolder {

    private PhotoActivity activity;

    @BindView(R.id.item_photo_base_landscape_btnBar)
    PhotoDownloadView downloadView;

    public static final int TYPE_BASE_LANDSCAPE = 9;

    public BaseLandscapeHolder(PhotoActivity a, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.activity = a;
    }

    @Override
    protected void onBindView(PhotoActivity a, Photo photo) {
        if (DatabaseHelper.getInstance(a).readDownloadingEntityCount(photo.id) > 0) {
            downloadView.setProgressState();
            activity.startCheckDownloadProgressThread();
        } else {
            downloadView.setButtonState();
        }
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

    public PhotoDownloadView getDownloadView() {
        return downloadView;
    }

    // interface.

    @OnClick(R.id.container_download_downloadBtn) void download() {
        activity.readyToDownload(DownloadHelper.DOWNLOAD_TYPE);
    }

    @OnClick(R.id.container_download_shareBtn) void share() {
        activity.readyToDownload(DownloadHelper.SHARE_TYPE);
    }

    @OnClick(R.id.container_download_wallBtn) void setWallpaper() {
        activity.readyToDownload(DownloadHelper.WALLPAPER_TYPE);
    }
}
