package com.wangdaye.mysplash._common.i.presenter;

/**
 * Swipe back manage presenter.
 * */

public interface SwipeBackManagePresenter {

    boolean checkCanSwipeBack(int dir);
    void swipeBackFinish();
}
