package com.wangdaye.mysplash._common.i.view;

/**
 * Search bar view.
 * */

public interface SearchBarView {

    void clearSearchBarText();
    void showKeyboard();
    void hideKeyboard();
    void submitSearchInfo(String text);
}
