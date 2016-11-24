package com.wangdaye.mysplash._common.i.view;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;

/**
 * Photo details view.
 * */

public interface PhotoDetailsView {

    void drawExif(Photo photo);

    void initRefreshStart();
    void requestDetailsSuccess();
}
