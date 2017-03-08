package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common._basic.MysplashActivity;

/**
 * Search bar presenter.
 * */

public interface SearchBarPresenter {

    void touchNavigatorIcon(MysplashActivity a);
    boolean touchMenuItem(MysplashActivity a, int itemId);

    void showKeyboard();
    void hideKeyboard();

    void submitSearchInfo(String text);
}
