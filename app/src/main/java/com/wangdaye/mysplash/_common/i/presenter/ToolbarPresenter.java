package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;

/**
 * Toolbar presenter.
 * */

public interface ToolbarPresenter {

    void touchNavigatorIcon(MysplashActivity a);
    void touchToolbar(MysplashActivity a);
    boolean touchMenuItem(MysplashActivity a, int itemId);
}
