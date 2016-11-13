package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;

/**
 * Multi-filter bar presenter.
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
