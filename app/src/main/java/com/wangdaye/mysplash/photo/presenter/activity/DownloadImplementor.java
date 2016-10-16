package com.wangdaye.mysplash.photo.presenter.activity;

import android.app.Activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.utils.DownloadHelper;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;

/**
 * Download implementor.
 * */

public class DownloadImplementor
        implements DownloadPresenter {
    // model & view.
    private DownloadModel model;

    /** <br> life cycle. */

    public DownloadImplementor(DownloadModel model) {
        this.model = model;
    }

    /** <br> presenter. */

    @Override
    public void download() {
        doDownload(DownloadHelper.DOWNLOAD_TYPE);
    }

    @Override
    public void share() {
        doDownload(DownloadHelper.SHARE_TYPE);
    }

    @Override
    public void setWallpaper() {
        doDownload(DownloadHelper.WALLPAPER_TYPE);
    }

    /** <br> utils. */

    private void doDownload(int type) {
        Activity a = Mysplash.getInstance().getTopActivity();
        DownloadHelper.getInstance(a).addMission(a, (Photo) model.getDownloadKey(), type);
    }
}