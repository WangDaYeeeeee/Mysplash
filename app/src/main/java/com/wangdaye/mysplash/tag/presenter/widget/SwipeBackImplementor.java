package com.wangdaye.mysplash.tag.presenter.widget;

import com.wangdaye.mysplash.common.i.presenter.SwipeBackPresenter;
import com.wangdaye.mysplash.common.i.view.SwipeBackView;

/**
 * Swipe back implementor.
 * */

public class SwipeBackImplementor
        implements SwipeBackPresenter {

    private SwipeBackView view;

    public SwipeBackImplementor(SwipeBackView view) {
        this.view = view;
    }

    @Override
    public boolean checkCanSwipeBack(int dir) {
        return view.checkCanSwipeBack(dir);
    }
}

