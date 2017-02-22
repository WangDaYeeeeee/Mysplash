package com.wangdaye.mysplash._common.i.view;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;

/**
 * Photo info view.
 * */

public interface PhotoInfoView {

    void touchMenuItem(int itemId);

    void requestPhotoSuccess(Photo photo);
    void requestPhotoFailed();
}
