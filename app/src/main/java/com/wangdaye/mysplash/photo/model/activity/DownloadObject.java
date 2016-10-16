package com.wangdaye.mysplash.photo.model.activity;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject
        implements DownloadModel {
    // data
    private Photo photo;

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
}
