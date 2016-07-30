package com.wangdaye.mysplash.photo.model.i;

/**
 * Download model
 * */

public interface DownloadModel {

    void setDownloading(boolean downloading);
    boolean isDownloading();

    void setDialogShowing(boolean showing);
    boolean isDialogShowing();

    void setDownloadType(int type);
    int getDownloadType();

    void setDownloadId(int id);
    int getDownloadId();
}
