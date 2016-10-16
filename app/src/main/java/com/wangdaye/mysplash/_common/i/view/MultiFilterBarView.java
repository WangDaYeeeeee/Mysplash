package com.wangdaye.mysplash._common.i.view;

/**
 * Multi-filter bar view.
 * */

public interface MultiFilterBarView {

    void touchNavigationIcon();
    void touchSearchButton();
    void touchMenuContainer(int position);
    void showKeyboard();
    void hideKeyboard();
    void submitSearchInfo();
}
