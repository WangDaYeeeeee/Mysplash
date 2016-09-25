package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;

/**
 * Multi-filter bar presenter.
 * */

public interface MultiFilterBarPresenter {
    void touchNavigatorIcon(Activity a);
    void touchToolbar(Activity a);
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
