package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;

/**
 * Auth response presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.MeManageView}.
 *
 * */

public interface MeManagePresenter {

    void touchMeAvatar(MysplashActivity a);
    void touchMeButton(MysplashActivity a);

    /** {@link com.wangdaye.mysplash.common.utils.manager.AuthManager#listenerList} */
    void responseWriteAccessToken();
    void responseWriteUserInfo();
    void responseWriteAvatarPath();
    void responseLogout();
}
