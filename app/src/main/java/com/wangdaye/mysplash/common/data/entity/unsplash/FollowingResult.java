package com.wangdaye.mysplash.common.data.entity.unsplash;

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
    public List<ActionObject> objects;
    public List<Collection> targets;

    public static final String VERB_LIKED = "liked";
    public static final String VERB_COLLECTED = "collected";
    public static final String VERB_FOLLOWED = "followed";
    public static final String VERB_RELEASE = "released";
    public static final String VERB_PUBLISHED = "published";
    public static final String VERB_CURATED = "curated";
}
