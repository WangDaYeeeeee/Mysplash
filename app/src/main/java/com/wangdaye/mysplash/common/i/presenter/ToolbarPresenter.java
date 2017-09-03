package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;

/**
 * Toolbar presenter.
 *
 * Presenter to control the behavior of {@link android.support.v7.widget.Toolbar}.
 *
 * */

public interface ToolbarPresenter {

    void touchNavigatorIcon(MysplashActivity a);
    void touchToolbar(MysplashActivity a);
    boolean touchMenuItem(MysplashActivity a, int itemId);
}
