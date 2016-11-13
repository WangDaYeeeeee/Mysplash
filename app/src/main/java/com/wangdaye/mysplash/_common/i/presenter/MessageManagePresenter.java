package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;

/**
 * Message mange presenter.
 * */

public interface MessageManagePresenter {

    void sendMessage(int what, Object o);
    void responseMessage(MysplashActivity a, int what, Object o);
}
