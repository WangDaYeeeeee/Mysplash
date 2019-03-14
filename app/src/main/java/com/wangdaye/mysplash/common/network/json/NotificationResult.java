package com.wangdaye.mysplash.common.network.json;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Notification result.
 * */

public class NotificationResult {

    /**
     * id : 6a05d000-f447-11e6-8080-8000693ad827
     * verb : followed
     * time : 2017-02-16T12:57:04.000000
     * is_seen : true
     * is_read : false
     * actors : [{"id":"WLvDY0UdN5c","updated_at":"2017-02-16T07:56:48-05:00","username":"wangdayeeeeee","name":"Wang DaYeeeeee","first_name":"Wang","last_name":"DaYeeeeee","portfolio_url":"https://github.com/WangDaYeeeeee/Mysplash","bio":"Developer for Mysplash.","location":"Qingdao, China","total_likes":0,"total_photos":0,"total_collections":0,"profile_image":{"small":"https://images.unsplash.com/profile-1485871774150-c1402dd1865a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=527aadb13e9caccf9b504b762e602644","medium":"https://images.unsplash.com/profile-1485871774150-c1402dd1865a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=5c52a3669cd24902cc6d8b47a2ec6f65","large":"https://images.unsplash.com/profile-1485871774150-c1402dd1865a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=a8a059ca698222a7ce7c1bdaf3bbeefd"},"links":{"self":"https://api.unsplash.com/users/wangdayeeeeee","html":"http://unsplash.com/@wangdayeeeeee","photos":"https://api.unsplash.com/users/wangdayeeeeee/photos","likes":"https://api.unsplash.com/users/wangdayeeeeee/likes","portfolio":"https://api.unsplash.com/users/wangdayeeeeee/portfolio","following":"https://api.unsplash.com/users/wangdayeeeeee/following","followers":"https://api.unsplash.com/users/wangdayeeeeee/followers"}}]
     * objects : [{"id":"Tdad39TgQmU","updated_at":"2017-04-06T21:13:21-04:00","username":"wangdaye","name":"Yueeeeee Wang","first_name":"Yueeeeee","last_name":"Wang","portfolio_url":"https://github.com/WangDaYeeeeee","bio":"Developer for Mysplash.\n\r\n🏀🎥🇨🇳💪📱💻💷🔩🗿🐸🐒","location":"Qingdao, China","total_likes":296,"total_photos":1,"total_collections":2,"profile_image":{"small":"https://images.unsplash.com/profile-1471692756546-97eafc2d979a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=88f263e32d6d6eb2722c6cd3da7a7185","medium":"https://images.unsplash.com/profile-1471692756546-97eafc2d979a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=161e0f02258eb6e26a372793441ee755","large":"https://images.unsplash.com/profile-1471692756546-97eafc2d979a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=574040b52f6a7a003466d1aa81d8838c"},"links":{"self":"https://api.unsplash.com/users/wangdaye","html":"http://unsplash.com/@wangdaye","photos":"https://api.unsplash.com/users/wangdaye/photos","likes":"https://api.unsplash.com/users/wangdaye/likes","portfolio":"https://api.unsplash.com/users/wangdaye/portfolio","following":"https://api.unsplash.com/users/wangdaye/following","followers":"https://api.unsplash.com/users/wangdaye/followers"}}]
     * targets : []
     */
    public String id;
    public String verb;
    public String time;
    public boolean is_seen;
    public boolean is_read;

    @Nullable public List<User> actors;
    @Nullable public List<ActionObject> objects;
    @Nullable public List<Collection> targets;

    public static final String VERB_LIKED = "liked";
    public static final String VERB_COLLECTED = "collected";
    public static final String VERB_FOLLOWED = "followed";
    public static final String VERB_RELEASE = "released";
    public static final String VERB_PUBLISHED = "published";
    public static final String VERB_CURATED = "curated";
}
