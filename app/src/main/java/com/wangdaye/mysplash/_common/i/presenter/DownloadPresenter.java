package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

/**
 * Download presenter.
 * */

public interface DownloadPresenter {

    void download(Context context);
    void share(Context context);
    void setWallpaper(Context context);

    Object getDownloadKey();
    void setDownloadKey(Object key);
}
