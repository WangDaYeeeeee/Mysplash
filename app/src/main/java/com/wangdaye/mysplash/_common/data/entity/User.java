package com.wangdaye.mysplash._common.data.entity;

import com.wangdaye.mysplash._common.utils.AuthManager;

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
    public String portfolio_url;
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

    public static User buildUser(Photo p) {
        User user = new User();
        user.username = p.user.username;
        user.name = p.user.name;
        user.profile_image = new ProfileImage();
        user.profile_image.large = p.user.profile_image.large;
        user.profile_image.medium = p.user.profile_image.medium;
        user.profile_image.small = p.user.profile_image.small;
        return user;
    }

    public static User buildUser(Collection c) {
        User user = new User();
        user.username = c.user.username;
        user.name = c.user.name;
        user.profile_image = new ProfileImage();
        user.profile_image.large = c.user.profile_image.large;
        user.profile_image.medium = c.user.profile_image.medium;
        user.profile_image.small = c.user.profile_image.small;
        return user;
    }

    public static User buildAuthUser() {
        User user = new User();
        user.username = AuthManager.getInstance().getUsername();
        user.name = AuthManager.getInstance().getFirstName() + " " + AuthManager.getInstance().getLastName();
        user.profile_image = new ProfileImage();
        user.profile_image.large = AuthManager.getInstance().getAvatarPath();
        user.profile_image.medium = AuthManager.getInstance().getAvatarPath();
        user.profile_image.small = AuthManager.getInstance().getAvatarPath();
        return user;
    }
}
