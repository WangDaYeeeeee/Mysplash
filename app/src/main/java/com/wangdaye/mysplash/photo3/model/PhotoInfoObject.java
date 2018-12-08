package com.wangdaye.mysplash.photo3.model;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel3;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter3;
import com.wangdaye.mysplash.photo3.view.activity.PhotoActivity3;

/**
 * Photo info object.
 * */

public class PhotoInfoObject
        implements PhotoInfoModel3 {

    private Photo photo;
    private boolean failed;

    private PhotoInfoAdapter3 adapter;
    private PhotoService photoService;

    public PhotoInfoObject(PhotoActivity3 a, Photo p, int columnCount) {
        setPhoto(p, true);
        this.failed = false;
        this.adapter = new PhotoInfoAdapter3(a, p, columnCount);
        this.photoService = PhotoService.getService();
    }

    @Override
    public PhotoService getPhotoService() {
        return photoService;
    }

    @Override
    public PhotoInfoAdapter3 getAdapter() {
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
