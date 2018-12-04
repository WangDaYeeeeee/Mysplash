package com.wangdaye.mysplash.photo2.model;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotoInfoModel2;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter2;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

/**
 * Photo info object.
 * */

public class PhotoInfoObject
        implements PhotoInfoModel2 {

    private Photo photo;
    private boolean failed;

    private PhotoInfoAdapter2 adapter;
    private PhotoService photoService;

    public PhotoInfoObject(PhotoActivity2 a, Photo p, int marginHorizontal, int columnCount) {
        setPhoto(p, true);
        this.failed = false;
        this.adapter = new PhotoInfoAdapter2(a, p, marginHorizontal, columnCount);
        this.photoService = PhotoService.getService();
    }

    @Override
    public PhotoService getPhotoService() {
        return photoService;
    }

    @Override
    public PhotoInfoAdapter2 getAdapter() {
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
