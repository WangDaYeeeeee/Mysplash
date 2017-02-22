package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoInfoService;

/**
 * Photo details model.
 * */

public interface PhotoDetailsModel {

    PhotoInfoService getService();

    Photo getPhoto();
    void setPhoto(Photo p);
}
