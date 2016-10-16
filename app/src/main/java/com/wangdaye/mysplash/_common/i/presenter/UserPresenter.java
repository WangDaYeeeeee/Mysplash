package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.data.entity.User;

/**
 * User presenter.
 * */

public interface UserPresenter {

    void requestUser();
    void cancelRequest();

    void setUser(User u);
    User getUser();
}
