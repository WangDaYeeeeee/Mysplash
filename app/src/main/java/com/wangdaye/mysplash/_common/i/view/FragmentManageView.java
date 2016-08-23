package com.wangdaye.mysplash._common.i.view;

import android.support.v4.app.Fragment;

/**
 * Fragment manage view.
 * */

public interface FragmentManageView {

    void addFragment(Fragment f);
    void popFragment();
    void changeFragment(Fragment f);
}
