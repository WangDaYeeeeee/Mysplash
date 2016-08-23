package com.wangdaye.mysplash._common.i.presenter;

/**
 * Download presenter.
 * */

public interface DownloadPresenter {

    void download();
    void share();
    void setWallpaper();

    void setDialogShowing(boolean showing);
    void cancelDownloading();
}
