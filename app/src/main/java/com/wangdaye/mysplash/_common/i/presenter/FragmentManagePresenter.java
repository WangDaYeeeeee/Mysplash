package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common._basic.MysplashFragment;

import java.util.List;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    List<MysplashFragment> getFragmentList(MysplashActivity a, boolean includeHidden);
    MysplashFragment getTopFragment(MysplashActivity a);

    void changeFragment(MysplashActivity a, int code, boolean init);

    int getId();
}
