package com.wangdaye.mysplash._common.data.entity.unsplash;

import java.util.List;

/**
 * Following result.
 * */

public class FollowingResult {
    // data
    public String id;
    public String verb;
    public String time;

    public List<User> actors;
    public List<Photo> objects;
    public List<Collection> targets;
}
