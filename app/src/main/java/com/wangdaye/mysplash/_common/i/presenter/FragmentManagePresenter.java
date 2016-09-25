package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    List<Fragment> getFragmentList();
    Fragment getTopFragment();

    void addFragment(Activity a, int code);
    void popFragment(Activity a);
    void changeFragment(Activity a, int code);
}
