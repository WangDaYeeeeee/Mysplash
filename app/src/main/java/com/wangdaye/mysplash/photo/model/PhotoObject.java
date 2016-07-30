package com.wangdaye.mysplash.photo.model;

import com.wangdaye.mysplash.common.data.model.SimplifiedPhoto;
import com.wangdaye.mysplash.photo.model.i.PhotoModel;

/**
 * Photo object.
 * */

public class PhotoObject
        implements PhotoModel {
    // data
    private SimplifiedPhoto photo;
    private String scale;

    public PhotoObject(SimplifiedPhoto p, String s) {
        photo = p;
        scale = s;
    }

    @Override
    public String getPhotoId() {
        return photo.id;
    }

    @Override
    public String getHtmlUrl() {
        return photo.url_html;
    }

    @Override
    public int getWidth() {
        return photo.width;
    }

    @Override
    public int getHeight() {
        return photo.height;
    }

    @Override
    public String getUserName() {
        return photo.user_name;
    }

    @Override
    public String getAvatarUrl() {
        return photo.url_user_avatar;
    }

    @Override
    public String getCreateTime() {
        return photo.created_at.split("T")[0];
    }

    @Override
    public String getRegularUrl() {
        return photo.url_regular;
    }

    // url.

    @Override
    public String selectDownloadUrl() {
        switch (scale) {
            case "compact":
                return photo.url_full;

            case "raw":
                return photo.url_raw;

            default:
                return photo.url_full;
        }
    }

    @Override
    public SimplifiedPhoto getPhoto() {
        return photo;
    }
}
