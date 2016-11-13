package com.wangdaye.mysplash._common.i.presenter;

import android.os.Bundle;

import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.fragment.SaveInstanceFragment;

import java.util.List;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    List<SaveInstanceFragment> getFragmentList();
    SaveInstanceFragment getTopFragment();

    List<Integer> getIdList();
    void clearIdList();

    void changeFragment(MysplashActivity a, Bundle saveInstanceState, int code);
    void addFragment(MysplashActivity a, Bundle saveInstanceState, int code);
    void popFragment(MysplashActivity a);

    int getFragmentCount();
}
