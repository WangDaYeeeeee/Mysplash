package com.wangdaye.mysplash.photo.model;

import com.wangdaye.mysplash.common.data.data.Photo;
import com.wangdaye.mysplash.photo.model.i.PhotoModel;

/**
 * Photo object.
 * */

public class PhotoObject
        implements PhotoModel {
    // data
    private Photo photo;
    private String scale;

    public PhotoObject(Photo p, String s) {
        photo = p;
        scale = s;
    }

    @Override
    public String getPhotoId() {
        return photo.id;
    }

    @Override
    public String getHtmlUrl() {
        return photo.links.html;
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
    public int getLikes() {
        return photo.likes;
    }

    @Override
    public String getUserName() {
        return photo.user.username;
    }

    @Override
    public String getAuthorName() {
        return photo.user.name;
    }

    @Override
    public String getAvatarUrl() {
        return photo.user.profile_image.large;
    }

    @Override
    public String getCreateTime() {
        return photo.created_at.split("T")[0];
    }

    @Override
    public String getRegularUrl() {
        return photo.urls.regular;
    }

    // url.

    @Override
    public String selectDownloadUrl() {
        switch (scale) {
            case "compact":
                return photo.urls.full;

            case "raw":
                return photo.urls.raw;

            default:
                return photo.urls.full;
        }
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }
}
