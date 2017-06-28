package com.wangdaye.mysplash.common.i.view;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;

/**
 * Photo info view.
 *
 * A view which can request {@link Photo} completely and show it.
 * When we get {@link Photo} data in bulk, it doesn't include
 * {@link Photo#exif}, {@link Photo#tags}, {@link Photo#categories}, {@link Photo#related_photos},
 * {@link Photo#related_collections}.
 *
 * */

public interface PhotoInfoView {

    void touchMenuItem(int itemId);

    void requestPhotoSuccess(Photo photo);
    void requestPhotoFailed();
    void setLikeForAPhotoCompleted();
}
