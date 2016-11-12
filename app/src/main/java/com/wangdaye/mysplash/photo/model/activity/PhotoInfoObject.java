package com.wangdaye.mysplash.photo.model.activity;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;

/**
 * Photo info object.
 * */

public class PhotoInfoObject
        implements PhotoInfoModel {
    // data
    private Photo photo;

    /** <br> life cycle. */

    public PhotoInfoObject(Photo p) {
        this.photo = p;
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }

    @Override
    public void setPhoto(Photo p) {
        this.photo = p;
    }
}
