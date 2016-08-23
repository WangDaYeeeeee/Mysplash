package com.wangdaye.mysplash._common.data.data;

/**
 * Category.
 * */

public class Category {

    /**
     * id : 2
     * title : Buildings
     * photo_count : 3428
     * links : {"self":"https://api.unsplash.com/categories/2","photos":"https://api.unsplash.com/categories/2/photos"}
     */

    public int id;
    public String title;
    public int photo_count;
    /**
     * self : https://api.unsplash.com/categories/2
     * photos : https://api.unsplash.com/categories/2/photos
     */

    public Links links;

    public static class Links {
        public String self;
        public String photos;
    }
}
