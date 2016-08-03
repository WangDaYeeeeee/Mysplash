package com.wangdaye.mysplash.photo.model.i;

import com.wangdaye.mysplash.common.data.data.Photo;
/**
 * Photo model.
 * */

public interface PhotoModel {

    String getPhotoId();
    String getHtmlUrl();
    int getWidth();
    int getHeight();
    int getLikes();
    String getUserName();
    String getAuthorName();
    String getAvatarUrl();
    String getCreateTime();
    String getRegularUrl();
    String selectDownloadUrl();

    Photo getPhoto();
}
