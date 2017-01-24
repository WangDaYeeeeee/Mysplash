package com.wangdaye.mysplash._common.i.presenter;

import android.os.Bundle;

import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui._basic.MysplashFragment;

import java.util.List;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    List<MysplashFragment> getFragmentList();
    MysplashFragment getTopFragment();

    List<Integer> getIdList();
    void clearIdList();

    void changeFragment(MysplashActivity a, Bundle saveInstanceState, int code);
    void addFragment(MysplashActivity a, Bundle saveInstanceState, int code);
    void popFragment(MysplashActivity a);

    int getFragmentCount();
}
