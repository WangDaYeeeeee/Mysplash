package com.wangdaye.mysplash.photo2.presenter;

import android.content.Context;

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
        doDownload(context, DownloaderService.DOWNLOAD_TYPE);
    }

    @Override
    public void share(Context context) {
        doDownload(context, DownloaderService.SHARE_TYPE);
    }

    @Override
    public void setWallpaper(Context context) {
        doDownload(context, DownloaderService.WALLPAPER_TYPE);
    }

    @Override
    public Object getDownloadKey() {
        return model.getDownloadKey();
    }

    @Override
    public void setDownloadKey(Object key) {
        model.setDownloadKey(key);
    }

    private void doDownload(Context context, int type) {
        Photo p = (Photo) model.getDownloadKey();
        DownloadHelper.getInstance(context).addMission(context, p, type);
    }
}