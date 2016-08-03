package com.wangdaye.mysplash.photo.presenter.i;

import android.content.Context;

/**
 * Download presenter.
 * */

public interface DownloadPresenter {

    void download(Context c);
    void share(Context c);
    void setWallpaper(Context c);

    void dismissDialog();
    void progressDialog(int p);
    void cancelDownload(Context c);

    int getDownloadId();
}
