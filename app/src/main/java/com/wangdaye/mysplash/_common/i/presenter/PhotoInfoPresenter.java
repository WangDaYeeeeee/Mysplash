package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.data.entity.Photo;

/**
 * Photo info presenter.
 * */

public interface PhotoInfoPresenter {

    void touchAuthorAvatar();
    void touchMenuItem(int itemId);

    Photo getPhoto();
}
