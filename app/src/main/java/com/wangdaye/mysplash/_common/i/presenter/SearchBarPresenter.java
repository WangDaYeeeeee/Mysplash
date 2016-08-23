package com.wangdaye.mysplash._common.i.presenter;

/**
 * Search bar presenter.
 * */

public interface SearchBarPresenter {

    void touchNavigatorIcon();
    void touchMenuItem(int itemId);
    void touchOrientationIcon();
    void touchSearchBar();

    void showKeyboard();
    void hideKeyboard();

    void setOrientation(String orientation);

    void submitSearchInfo(String text);
}
