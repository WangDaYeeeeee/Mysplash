package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common._basic.MysplashActivity;

/**
 * Swipe back manage presenter.
 * */

public interface SwipeBackManagePresenter {

    boolean checkCanSwipeBack(int dir);
    void swipeBackFinish(MysplashActivity a, int dir);
}
