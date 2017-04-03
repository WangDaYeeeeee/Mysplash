package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common.data.entity.unsplash.User;

/**
 * User presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.UserView}.
 *
 * */

public interface UserPresenter {

    void requestUser();
    void followUser();
    void cancelFollowUser();
    void cancelRequest();

    void setUser(User u);
    User getUser();
}
