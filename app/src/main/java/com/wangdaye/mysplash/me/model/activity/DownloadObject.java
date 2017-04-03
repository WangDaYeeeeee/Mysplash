package com.wangdaye.mysplash.me.model.activity;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject implements DownloadModel {
    // data
    private Photo photo;

    /** <br> model. */

    @Override
    public Object getDownloadKey() {
        return photo;
    }

    @Override
    public void setDownloadKey(Object key) {
        this.photo = (Photo) key;
    }
}
