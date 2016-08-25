package com.wangdaye.mysplash._common.i.presenter;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    List<Fragment> getFragmentList();

    void addFragment(int code);
    void popFragment();
    void changeFragment(int code);
}
