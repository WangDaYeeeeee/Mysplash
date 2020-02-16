package com.wangdaye.base.unsplash;

import java.io.Serializable;

/**
 * Change collection photo result.
 * */

public class ChangeCollectionPhotoResult implements Serializable {

    /**
     * created_at : 2016-02-29T15:47:39.969-05:00
     */
    public String created_at;

    public Photo photo;
    public Collection collection;
    public User user;
}
