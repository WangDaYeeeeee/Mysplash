package com.wangdaye.mysplash.collection.presenter.activity;

import android.content.Context;

import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.downloader.DownloaderService;
import com.wangdaye.mysplash.common.i.model.DownloadModel;
import com.wangdaye.mysplash.common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash.common.utils.helper.DownloadHelper;

/**
 * Download implementor.
 * */

public class DownloadImplementor
        implements DownloadPresenter {

    private DownloadModel model;

    public DownloadImplementor(DownloadModel model) {
        this.model = model;
    }

    @Override
    public void download(Context context) {
        Object key = getDownloadKey();
        if (key instanceof Collection) {
            DownloadHelper.getInstance(context).addMission(context, ((Collection) key));
        } else {
            DownloadHelper.getInstance(context).addMission(context, (Photo) key, DownloaderService.DOWNLOAD_TYPE);
        }
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
