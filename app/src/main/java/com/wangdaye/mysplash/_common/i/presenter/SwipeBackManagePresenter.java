package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;

/**
 * Swipe back manage presenter.
 * */

public interface SwipeBackManagePresenter {

    boolean checkCanSwipeBack(int dir);
    void swipeBackFinish(Activity a, int dir);
}
