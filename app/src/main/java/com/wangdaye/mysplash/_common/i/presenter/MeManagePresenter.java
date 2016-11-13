package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;

/**
 * Auth response presenter.
 * */

public interface MeManagePresenter {

    void touchMeAvatar(MysplashActivity a);
    void touchMeButton(MysplashActivity a);

    void responseWriteAccessToken();
    void responseWriteUserInfo();
    void responseWriteAvatarPath();
    void responseLogout();
}
