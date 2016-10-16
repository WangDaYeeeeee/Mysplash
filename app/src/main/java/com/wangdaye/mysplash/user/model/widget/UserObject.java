package com.wangdaye.mysplash.user.model.widget;

import com.wangdaye.mysplash._common.data.entity.User;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.i.model.UserModel;

/**
 * User object.
 * */

public class UserObject
        implements UserModel {
    // data
    private UserService service;
    private User user = null;

    /** <br> life cycle. */

    public UserObject() {
        service = UserService.getService();
    }

    /** <br> model. */

    @Override
    public UserService getService() {
        return service;
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
