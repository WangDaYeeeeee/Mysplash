package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoInfoService;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;

/**
 * Photo info model.
 * */

public interface PhotoInfoModel {

    PhotoInfoService getPhotoInfoService();
    PhotoService getPhotoService();
    PhotoInfoAdapter getAdapter();

    Photo getPhoto();
    void setPhoto(Photo p);

    boolean isFailed();
    void setFailed(boolean b);
}
