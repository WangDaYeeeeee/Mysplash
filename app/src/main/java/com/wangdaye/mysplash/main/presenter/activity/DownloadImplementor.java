package com.wangdaye.mysplash.main.presenter.activity;

import android.content.Context;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;

/**
 * Download implementor.
 *
 * A {@link DownloadPresenter} for {@link com.wangdaye.mysplash.main.view.activity.MainActivity}.
 *
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
    public void download(Context context) {
        Photo p = (Photo) model.getDownloadKey();
        DownloadHelper.getInstance(context).addMission(context, p, DownloadHelper.DOWNLOAD_TYPE);
    }

    @Override
    public void share(Context context) {
        // do nothing.
    }

    @Override
    public void setWallpaper(Context context) {
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
