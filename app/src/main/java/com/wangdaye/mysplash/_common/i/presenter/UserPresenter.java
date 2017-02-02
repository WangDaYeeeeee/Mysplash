package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.data.entity.unsplash.User;

/**
 * User presenter.
 * */

public interface UserPresenter {

    void requestUser();
    void followUser();
    void cancelFollowUser();
    void cancelRequest();

    void setUser(User u);
    User getUser();


}
