package com.wangdaye.mysplash._common.i.view;

/**
 * Multi-filter bar view.
 * */

public interface MultiFilterBarView {

    void touchMenuContainer(int position);
    void showKeyboard();
    void hideKeyboard();
    void submitSearchInfo(int categoryId, boolean featured,
                          String username, String query,
                          String orientation);
}
