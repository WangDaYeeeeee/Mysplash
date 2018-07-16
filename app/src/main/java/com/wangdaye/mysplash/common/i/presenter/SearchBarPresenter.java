package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;

/**
 * Search bar presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.SearchBarView}.
 *
 * */

public interface SearchBarPresenter {

    void touchNavigatorIcon(MysplashActivity a);
    boolean touchMenuItem(MysplashActivity a, int itemId);

    void showKeyboard();
    void hideKeyboard();

    void submitSearchInfo(String text);
}
