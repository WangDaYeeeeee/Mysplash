package com.wangdaye.mysplash.photo.presenter;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
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

    @Override
    public Object getDownloadKey() {
        return model.getDownloadKey();
    }

    @Override
    public void setDownloadKey(Object key) {
        model.setDownloadKey(key);
    }

    /** <br> utils. */

    private void doDownload(int type) {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        Photo p = (Photo) model.getDownloadKey();
        DownloadHelper.getInstance(a).addMission(a, p, type);
    }
}