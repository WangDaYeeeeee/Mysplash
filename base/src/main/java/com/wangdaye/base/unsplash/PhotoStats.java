package com.wangdaye.base.unsplash;

import java.io.Serializable;

/**
 * Photo stats.
 * */

public class PhotoStats implements Serializable {

    /**
     * downloads : 11068
     * likes : 195
     * views : 428842
     * links : {"self":"https://api.unsplash.com/photos/x8R2oSWZRSE","html":"https://unsplash.com/photos/x8R2oSWZRSE","download":"https://unsplash.com/photos/x8R2oSWZRSE/download"}
     */
    public int downloads;
    public int likes;
    public int views;

    public PhotoLinks links;
}
