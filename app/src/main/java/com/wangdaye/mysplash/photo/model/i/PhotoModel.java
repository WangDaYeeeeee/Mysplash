package com.wangdaye.mysplash.photo.model.i;

import com.wangdaye.mysplash.common.data.model.SimplifiedPhoto;

/**
 * Photo model.
 * */

public interface PhotoModel {

    String getPhotoId();
    String getHtmlUrl();
    int getWidth();
    int getHeight();
    String getUserName();
    String getAvatarUrl();
    String getCreateTime();
    String getRegularUrl();
    String selectDownloadUrl();

    SimplifiedPhoto getPhoto();
}
