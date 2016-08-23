package com.wangdaye.mysplash._common.i.model;

/**
 * Download model.
 * */

public interface DownloadModel {

    Object getDownloadKey();
    void setDownloadKey(Object key);

    boolean isDownloading();
    void setDownloading(boolean downloading);

    boolean isDialogShowing();
    void setDialogShowing(boolean showing);

    int getDownloadId();
    void setDownloadId(int id);

    int getDownloadType();
    void setDownloadType(int type);
}
