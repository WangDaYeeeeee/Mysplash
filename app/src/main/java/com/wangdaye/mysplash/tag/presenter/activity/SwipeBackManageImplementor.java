package com.wangdaye.mysplash.tag.presenter.activity;

import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common._basic.MysplashActivity;

/**
 * Swipe back manage implementor.
 * */

public class SwipeBackManageImplementor
        implements SwipeBackManagePresenter {
    // model & view.
    private SwipeBackManageView view;

    /** <br> life cycle. */

    public SwipeBackManageImplementor(SwipeBackManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public boolean checkCanSwipeBack(int dir) {
        return view.checkCanSwipeBack(dir);
    }

    @Override
    public void swipeBackFinish(MysplashActivity a, int dir) {
        a.finishActivity(dir);
    }
}

