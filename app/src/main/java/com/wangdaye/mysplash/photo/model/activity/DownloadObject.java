package com.wangdaye.mysplash.photo.model.activity;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject
        implements DownloadModel {
    // data
    private Photo photo;

    /** <br> life cycle. */

    public DownloadObject(Photo p) {
        this.photo = p;
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
