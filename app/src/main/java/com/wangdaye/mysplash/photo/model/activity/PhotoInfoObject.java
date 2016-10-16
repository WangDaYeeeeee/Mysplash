package com.wangdaye.mysplash.photo.model.activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.entity.PhotoDetails;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;

/**
 * Photo info object.
 * */

public class PhotoInfoObject
        implements PhotoInfoModel {
    // data
    private Photo photo;
    private PhotoDetails photoDetails;

    /** <br> life cycle. */

    public PhotoInfoObject() {
        photo = Mysplash.getInstance().getPhoto();
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }

    @Override
    public PhotoDetails getPhotoDetails() {
        return photoDetails;
    }

    @Override
    public void setPhotoDetails(PhotoDetails details) {
        photoDetails = details;
    }
}
