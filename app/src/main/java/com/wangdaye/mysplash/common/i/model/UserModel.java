package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.FollowingService;
import com.wangdaye.mysplash.common.data.service.UserService;

/**
 * User model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.UserView}.
 *
 * */

public interface UserModel {

    UserService getUserService();
    FollowingService getFollowingService();

    User getUser();
    void setUser(User user);
}
