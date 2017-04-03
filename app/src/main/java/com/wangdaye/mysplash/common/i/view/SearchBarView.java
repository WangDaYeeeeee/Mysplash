package com.wangdaye.mysplash.common.i.view;

/**
 * Search bar view.
 *
 * App bar view for {@link SearchView}.
 *
 * */

public interface SearchBarView {

    void clearSearchBarText();
    void showKeyboard();
    void hideKeyboard();
    void submitSearchInfo(String text);
}
