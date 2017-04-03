package com.wangdaye.mysplash.me.model.activity;

import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.FollowingService;
import com.wangdaye.mysplash.common.data.service.UserService;
import com.wangdaye.mysplash.common.i.model.UserModel;

/**
 * User object.
 * */

public class UserObject
        implements UserModel {
    // data
    private UserService userService;
    private User user = null;

    /** <br> life cycle. */

    public UserObject() {
        userService = UserService.getService();
    }

    /** <br> model. */

    @Override
    public UserService getUserService() {
        return userService;
    }

    public FollowingService getFollowingService() {
        return null;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
