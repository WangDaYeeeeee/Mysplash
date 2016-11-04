package com.wangdaye.mysplash.photo.model.widget;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.PhotoDetailsModel;

/**
 * Photo details object.
 * */

public class PhotoDetailsObject
        implements PhotoDetailsModel {
    // data
    private PhotoService service;
    private Photo photo;

    /** <br> life cycle. */

    public PhotoDetailsObject(Photo p) {
        service = PhotoService.getService();
        photo = p;
    }

    /** <br> model. */

    @Override
    public PhotoService getService() {
        return service;
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }

    @Override
    public void setPhoto(Photo p) {
        photo = p;
    }
}
