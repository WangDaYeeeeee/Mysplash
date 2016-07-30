package com.wangdaye.mysplash.photo.model;

import com.wangdaye.mysplash.photo.model.i.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject
        implements DownloadModel {
    // data
    private int downloadId;
    private boolean downloading = false;
    private boolean dialogShowing = false;

    private int downloadType;
    public static final int SIMPLE_DOWNLOAD_TYPE = 1;
    public static final int SHARE_DOWNLOAD_TYPE = 2;
    public static final int WALL_DOWNLOAD_TYPE = 3;

    /** <br> model. */

    // downloading.

    @Override
    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    @Override
    public boolean isDownloading() {
        return downloading;
    }

    // dialog showing.

    @Override
    public void setDialogShowing(boolean showing) {
        this.dialogShowing = showing;
    }

    @Override
    public boolean isDialogShowing() {
        return dialogShowing;
    }

    // download type.

    @Override
    public void setDownloadType(int type) {
        this.downloadType = type;
    }

    @Override
    public int getDownloadType() {
        return downloadType;
    }

    // download id.

    @Override
    public void setDownloadId(int id) {
        this.downloadId = id;
    }

    @Override
    public int getDownloadId() {
        return downloadId;
    }
}
