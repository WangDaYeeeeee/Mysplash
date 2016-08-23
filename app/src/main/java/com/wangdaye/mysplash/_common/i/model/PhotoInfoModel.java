package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.data.PhotoDetails;

/**
 * Photo info model.
 * */

public interface PhotoInfoModel {

    Photo getPhoto();

    PhotoDetails getPhotoDetails();
    void setPhotoDetails(PhotoDetails details);
}
