package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;

/**
 * Search bar presenter.
 * */

public interface SearchBarPresenter {

    void touchNavigatorIcon(Activity a);
    boolean touchMenuItem(Activity a, int itemId);

    void showKeyboard();
    void hideKeyboard();

    void submitSearchInfo(String text);
}
