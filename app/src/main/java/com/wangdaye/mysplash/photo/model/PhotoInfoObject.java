package com.wangdaye.mysplash.photo.model;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoInfoService;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

/**
 * Photo info object.
 * */

public class PhotoInfoObject
        implements PhotoInfoModel {

    private Photo photo;
    private boolean failed;
    private PhotoInfoAdapter adapter;

    private PhotoInfoService photoInfoService;
    private PhotoService photoService;

    public PhotoInfoObject(PhotoActivity a, Photo p) {
        setPhoto(p, true);
        this.failed = false;
        this.adapter = new PhotoInfoAdapter(a, p);
        this.photoInfoService = PhotoInfoService.getService();
        this.photoService = PhotoService.getService();
    }

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
    public void setPhoto(Photo p, boolean init) {
        if (init && p != null) {
            p.settingLike = false;
        }
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
