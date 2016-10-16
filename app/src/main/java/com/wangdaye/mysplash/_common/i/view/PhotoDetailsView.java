package com.wangdaye.mysplash._common.i.view;

import com.wangdaye.mysplash._common.data.entity.PhotoDetails;

/**
 * Photo details view.
 * */

public interface PhotoDetailsView {

    void drawExif(PhotoDetails details);

    void initRefreshStart();
    void requestDetailsSuccess();
}
