package com.wangdaye.mysplash.collection.presenter.activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;

/**
 * Download implementor.
 * */

public class DownloadImplementor implements DownloadPresenter {
    // model & view.
    private DownloadModel model;

    /** <br> life cycle. */

    public DownloadImplementor(DownloadModel model) {
        this.model = model;
    }

    /** <br> presenter. */

    @Override
    public void download() {
        MysplashActivity a = Mysplash.getInstance().getTopActivity();
        Object key = getDownloadKey();
        if (key instanceof Collection) {
            DownloadHelper.getInstance(a).addMission(a, ((Collection) key));
        } else {
            DownloadHelper.getInstance(a).addMission(a, (Photo) key, DownloadHelper.DOWNLOAD_TYPE);
        }
    }

    @Override
    public void share() {
        // do nothing.
    }

    @Override
    public void setWallpaper() {
        // do nothing.
    }

    @Override
    public Object getDownloadKey() {
        return model.getDownloadKey();
    }

    @Override
    public void setDownloadKey(Object key) {
        model.setDownloadKey(key);
    }
}
