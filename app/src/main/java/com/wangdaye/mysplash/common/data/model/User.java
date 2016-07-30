package com.wangdaye.mysplash.common.data.model;

/**
 * User.
 * */

public class User {

    /**
     * username : jimmyexample
     * name : James Example
     * first_name : James
     * last_name : Example
     * portfolio_url : null
     * bio : The user's bio
     * location : Montreal, Qc
     * total_likes : 20
     * total_photos : 10
     * total_collections : 5
     * downloads : 225974
     * profile_image : {"small":"https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}
     * badge : {"title":"Book contributor","primary":true,"slug":"book-contributor","link":"https://book.unsplash.com"}
     * links : {"self":"https://api.unsplash.com/users/jimmyexample","html":"https://unsplash.com/jimmyexample","photos":"https://api.unsplash.com/users/jimmyexample/photos","likes":"https://api.unsplash.com/users/jimmyexample/likes"}
     */

    public String username;
    public String name;
    public String first_name;
    public String last_name;
    public Object portfolio_url;
    public String bio;
    public String location;
    public int total_likes;
    public int total_photos;
    public int total_collections;
    public int downloads;
    /**
     * small : https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32
     * medium : https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64
     * large : https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128
     */

    public ProfileImage profile_image;
    /**
     * title : Book contributor
     * primary : true
     * slug : book-contributor
     * link : https://book.unsplash.com
     */

    public Badge badge;
    /**
     * self : https://api.unsplash.com/users/jimmyexample
     * html : https://unsplash.com/jimmyexample
     * photos : https://api.unsplash.com/users/jimmyexample/photos
     * likes : https://api.unsplash.com/users/jimmyexample/likes
     */

    public Links links;

    public static class ProfileImage {
        public String small;
        public String medium;
        public String large;
    }

    public static class Badge {
        public String title;
        public boolean primary;
        public String slug;
        public String link;
    }

    public static class Links {
        public String self;
        public String html;
        public String photos;
        public String likes;
    }
}
