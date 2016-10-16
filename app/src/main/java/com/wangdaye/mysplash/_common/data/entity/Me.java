package com.wangdaye.mysplash._common.data.entity;

/**
 * Me.
 * */

public class Me {

    /**
     * username : jimmyexample
     * first_name : James
     * last_name : Example
     * portfolio_url : http://unsplash.com/crew
     * bio : The user's bio
     * location : Montreal, Qc
     * total_likes : 20
     * total_photos : 10
     * total_collections : 5
     * downloads : 4321
     * uploads_remaining : 4
     * instagram_username : james-example
     * email : jim@example.com
     * links : {"self":"https://api.unsplash.com/users/jimmyexample","html":"https://unsplash.com/jimmyexample","photos":"https://api.unsplash.com/users/jimmyexample/photos","likes":"https://api.unsplash.com/users/jimmyexample/likes"}
     */

    public String username;
    public String first_name;
    public String last_name;
    public String portfolio_url;
    public String bio;
    public String location;
    public int total_likes;
    public int total_photos;
    public int total_collections;
    public int downloads;
    public int uploads_remaining;
    public String instagram_username;
    public String email;
    /**
     * self : https://api.unsplash.com/users/jimmyexample
     * html : https://unsplash.com/jimmyexample
     * photos : https://api.unsplash.com/users/jimmyexample/photos
     * likes : https://api.unsplash.com/users/jimmyexample/likes
     */

    public Links links;

    public static class Links {
        public String self;
        public String html;
        public String photos;
        public String likes;
    }
}
