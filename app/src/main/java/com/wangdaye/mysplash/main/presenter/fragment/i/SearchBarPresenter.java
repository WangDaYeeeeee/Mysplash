package com.wangdaye.mysplash.main.presenter.fragment.i;

import android.content.Context;
import android.view.View;

/**
 * Search bar presenter.
 * */

public interface SearchBarPresenter {

    void clickNavigationIcon();
    void clickMenuItem(int id);
    void showOrientationMenu(Context c, View anchor);
    void clickSearchBar();
    void inputSearchQuery(String text);
}
