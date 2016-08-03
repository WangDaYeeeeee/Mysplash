package com.wangdaye.mysplash.main.view.activity.i;

import android.support.v4.app.Fragment;

/**
 * Drawer view.
 * */

public interface DrawerView {

    void changeFragment(Fragment f);
    void addFragment(Fragment f);
    void removeFragment();

    void closeDrawer();
    void sendMessage(int what);

    void reboot();
}
