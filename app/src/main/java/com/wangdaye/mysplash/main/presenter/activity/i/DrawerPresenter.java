package com.wangdaye.mysplash.main.presenter.activity.i;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Drawer presenter.
 * */

public interface DrawerPresenter {

    void selectDrawerItem(int id);

    void changeFragment(Fragment f);
    void addFragment(Fragment f);
    void removeFragment();
    void clearFragment();

    void processMessage(Context c, int what);
}
