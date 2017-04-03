package com.wangdaye.mysplash.photo.model;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.DownloadModel;

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
