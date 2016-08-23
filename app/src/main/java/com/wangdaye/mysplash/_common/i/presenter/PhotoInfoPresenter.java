package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.data.data.Photo;
import com.wangdaye.mysplash._common.data.data.PhotoDetails;

/**
 * Photo info presenter.
 * */

public interface PhotoInfoPresenter {

    void touchAuthorAvatar();
    void touchMenuItem(int itemId);

    void drawPhotoDetails();
    Photo getPhoto();
    PhotoDetails getPhotoDetails();
}
