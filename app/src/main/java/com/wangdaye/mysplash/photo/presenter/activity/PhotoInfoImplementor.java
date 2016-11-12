package com.wangdaye.mysplash.photo.presenter.activity;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.i.model.PhotoInfoModel;
import com.wangdaye.mysplash._common.i.presenter.PhotoInfoPresenter;
import com.wangdaye.mysplash._common.i.view.PhotoInfoView;

/**
 * Photo info implementor.
 * */

public class PhotoInfoImplementor
        implements PhotoInfoPresenter {
    // model & view.
    private PhotoInfoModel model;
    private PhotoInfoView view;

    /** <br> life cycle. */

    public PhotoInfoImplementor(PhotoInfoModel model, PhotoInfoView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void touchAuthorAvatar() {
        view.touchAuthorAvatar();
    }

    @Override
    public void touchMenuItem(int itemId) {
        view.touchMenuItem(itemId);
    }

    @Override
    public Photo getPhoto() {
        return model.getPhoto();
    }
}
