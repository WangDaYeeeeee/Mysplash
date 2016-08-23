package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;

/**
 * Auth response presenter.
 * */

public interface MeManagePresenter {

    void touchMeAvatar(Activity a);
    void touchMeButton(Activity a);

    void responseWriteAccessToken();
    void responseWriteUserInfo();
    void responseWriteAvatarPath();
    void responseLogout();
}
