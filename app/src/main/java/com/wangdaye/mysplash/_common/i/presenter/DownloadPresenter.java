package com.wangdaye.mysplash._common.i.presenter;

/**
 * Download presenter.
 * */

public interface DownloadPresenter {

    void download();
    void share();
    void setWallpaper();

    Object getDownloadKey();
    void setDownloadKey(Object key);
}
