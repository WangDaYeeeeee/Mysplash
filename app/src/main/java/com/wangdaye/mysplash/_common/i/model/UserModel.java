package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.data.service.UserService;

/**
 * User model.
 * */

public interface UserModel {

    UserService getService();

    User getUser();
    void setUser(User user);
}
