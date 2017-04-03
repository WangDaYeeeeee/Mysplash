package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common._basic.MysplashActivity;

/**
 * Multi-filter bar presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.MultiFilterBarView}.
 *
 * */

public interface MultiFilterBarPresenter {
    void touchNavigatorIcon();
    void touchToolbar(MysplashActivity a);
    void touchSearchButton();
    void touchMenuContainer(int position);

    void showKeyboard();
    void hideKeyboard();

    void submitSearchInfo();

    void setQuery(String query);
    String getQuery();

    void setUsername(String username);
    String getUsername();

    void setCategory(int c);
    int getCategory();

    void setOrientation(String o);
    String getOrientation();

    void setFeatured(boolean f);
    boolean isFeatured();
}
