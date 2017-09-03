package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;

/**
 * Swipe back manage presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.SwipeBackManageView}.
 *
 * */

public interface SwipeBackManagePresenter {

    boolean checkCanSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir);
    void swipeBackFinish(MysplashActivity a, @SwipeBackCoordinatorLayout.DirectionRule int dir);
}
