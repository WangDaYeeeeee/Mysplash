package com.wangdaye.mysplash.main.view.activity.i;

import android.support.v4.app.Fragment;

/**
 * Fragment manage view.
 * */

public interface FragmentManageView {

    void changeFragment(Fragment f);
    void addFragment(Fragment f);
    void removeFragment();

    void closeDrawer();
    void sendMessage(int what);
}
