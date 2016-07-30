package com.wangdaye.mysplash.main.view.fragment.i;

/**
 * Search bar view.
 * */

public interface SearchBarView {

    void clickNavigationIcon();
    void clearSearchText();
    void changeOrientation(String orientation);
    void scrollToTop();
    void inputSearchQuery(String query, String orientation);
    void showKeyboard();
    void hideKeyboard();
}
