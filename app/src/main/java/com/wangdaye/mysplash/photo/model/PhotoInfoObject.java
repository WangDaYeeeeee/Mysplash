package com.wangdaye.mysplash.photo.model;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoInfoService;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

/**
 * Photo info object.
 * */

public class PhotoInfoObject
        implements PhotoInfoModel {
    // data
    private Photo photo;
    private boolean failed;
    private PhotoInfoAdapter adapter;
    private PhotoInfoService photoInfoService;
    private PhotoService photoService;

    /** <br> life cycle. */

    public PhotoInfoObject(PhotoActivity a, Photo p) {
        this.photo = p;
        this.failed = false;
        this.adapter = new PhotoInfoAdapter(a, p);
        this.photoInfoService = PhotoInfoService.getService();
        this.photoService = PhotoService.getService();
    }

    /** <br> model. */

    @Override
    public PhotoInfoService getPhotoInfoService() {
        return photoInfoService;
    }

    @Override
    public PhotoService getPhotoService() {
        return photoService;
    }

    @Override
    public PhotoInfoAdapter getAdapter() {
        return adapter;
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }

    @Override
    public void setPhoto(Photo p) {
        this.photo = p;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @Override
    public void setFailed(boolean b) {
        this.failed = b;
    }
}
