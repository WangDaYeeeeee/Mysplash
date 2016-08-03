package com.wangdaye.mysplash.common.data.data;

/**
 * Photo stats.
 * */

public class PhotoStats {

    /**
     * downloads : 11068
     * likes : 195
     * views : 428842
     * links : {"self":"https://api.unsplash.com/photos/x8R2oSWZRSE","html":"https://unsplash.com/photos/x8R2oSWZRSE","download":"https://unsplash.com/photos/x8R2oSWZRSE/download"}
     */

    public int downloads;
    public int likes;
    public int views;
    /**
     * self : https://api.unsplash.com/photos/x8R2oSWZRSE
     * html : https://unsplash.com/photos/x8R2oSWZRSE
     * download : https://unsplash.com/photos/x8R2oSWZRSE/download
     */

    public Links links;

    public static class Links {
        public String self;
        public String html;
        public String download;
    }
}
