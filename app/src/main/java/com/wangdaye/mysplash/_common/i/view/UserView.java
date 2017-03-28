package com.wangdaye.mysplash._common.i.view;

import com.wangdaye.mysplash._common.data.entity.unsplash.User;

/**
 * User view.
 * */

public interface UserView {

    void initRefreshStart();
    void drawUserInfo(User user);

    void followRequestSuccess(boolean follow);
    void followRequestFailed(boolean follow);
}
