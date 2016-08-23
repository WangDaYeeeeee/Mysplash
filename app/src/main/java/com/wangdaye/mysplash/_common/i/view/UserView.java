package com.wangdaye.mysplash._common.i.view;

import com.wangdaye.mysplash._common.data.data.User;

/**
 * User view.
 * */

public interface UserView {

    void drawUserInfo(User user);

    void initRefreshStart();
    void requestDetailsSuccess();
    void requestDetailsFailed();
}
