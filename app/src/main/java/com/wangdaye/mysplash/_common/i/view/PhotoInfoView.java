package com.wangdaye.mysplash._common.i.view;

import com.wangdaye.mysplash._common.data.entity.PhotoDetails;

/**
 * Photo info view.
 * */

public interface PhotoInfoView {

    void touchAuthorAvatar();
    void touchMenuItem(int itemId);

    void drawPhotoDetails(PhotoDetails details);
}
