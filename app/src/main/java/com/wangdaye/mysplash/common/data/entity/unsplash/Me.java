package com.wangdaye.mysplash.common.data.entity.unsplash;

/**
 * Me.
 * */

public class Me {

    /**
     * id : pXhwzz1JtQU
     * username : jimmyexample
     * first_name : James
     * last_name : Example
     * portfolio_url : null
     * bio : The user's bio
     * location : Montreal, Qc
     * total_likes : 20
     * total_photos : 10
     * total_collections : 5
     * followed_by_user : false
     * downloads : 4321
     * uploads_remaining : 4
     * instagram_username : james-example
     * email : jim@example.com
     * links : {"self":"https://api.unsplash.com/users/jimmyexample","html":"https://unsplash.com/jimmyexample","photos":"https://api.unsplash.com/users/jimmyexample/photos","likes":"https://api.unsplash.com/users/jimmyexample/likes","portfolio":"https://api.unsplash.com/users/jimmyexample/portfolio"}
     */
    public String id;
    public String username;
    public String first_name;
    public String last_name;
    public String portfolio_url;
    public String bio;
    public String location;
    public int total_likes;
    public int total_photos;
    public int total_collections;
    public boolean followed_by_user;
    public int downloads;
    public int uploads_remaining;
    public String instagram_username;
    public String email;

    public UserLinks links;
}
