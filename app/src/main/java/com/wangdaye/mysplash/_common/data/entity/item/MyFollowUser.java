package com.wangdaye.mysplash._common.data.entity.item;

import com.wangdaye.mysplash._common.data.entity.unsplash.User;

/**
 * My follow user.
 * */

public class MyFollowUser {
    // data
    public boolean requesting;
    public boolean switchTo;
    public User user;

    /** <br> life cycle. */

    public MyFollowUser(User u) {
        this.requesting = false;
        this.switchTo = false;
        this.user = u;
    }
}
