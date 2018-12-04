package com.wangdaye.mysplash.user.model.widget;

import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.network.FeedService;
import com.wangdaye.mysplash.common.data.service.network.UserService;
import com.wangdaye.mysplash.common.i.model.UserModel;

/**
 * User object.
 * */

public class UserObject
        implements UserModel {

    private UserService userService;
    private FeedService feedService;
    private User user = null;

    public UserObject() {
        userService = UserService.getService();
        feedService = FeedService.getService();
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    public FeedService getFeedService() {
        return feedService;
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
