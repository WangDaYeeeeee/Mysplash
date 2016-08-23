package com.wangdaye.mysplash.photo.model.activity;

import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject
        implements DownloadModel {
    // data
    private Photo photo;

    private boolean downloading = false;
    private boolean dialogShowing = false;
    private int downloadId;

    private int downloadType;
    public static final int DOWNLOAD_TYPE = 0;
    public static final int SHARE_TYPE = 1;
    public static final int WALLPAPER_TYPE = 2;

    /** <br> life cycle. */

    public DownloadObject(Photo photo) {
        this.photo = photo;
    }

    /** <br> model. */

    @Override
    public Object getDownloadKey() {
        return photo;
    }

    @Override
    public void setDownloadKey(Object key) {
        photo = (Photo) key;
    }

    @Override
    public boolean isDownloading() {
        return downloading;
    }

    @Override
    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    @Override
    public boolean isDialogShowing() {
        return dialogShowing;
    }

    @Override
    public void setDialogShowing(boolean showing) {
        dialogShowing = showing;
    }

    @Override
    public int getDownloadId() {
        return downloadId;
    }

    @Override
    public void setDownloadId(int id) {
        downloadId = id;
    }

    @Override
    public int getDownloadType() {
        return downloadType;
    }

    @Override
    public void setDownloadType(int type) {
        downloadType = type;
    }
}
