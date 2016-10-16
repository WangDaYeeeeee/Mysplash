package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.entity.PhotoDetails;

/**
 * Photo info model.
 * */

public interface PhotoInfoModel {

    Photo getPhoto();

    PhotoDetails getPhotoDetails();
    void setPhotoDetails(PhotoDetails details);
}
