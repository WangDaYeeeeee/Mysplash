package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;

/**
 * Swipe back presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.SwipeBackView}.
 *
 * */

public interface SwipeBackPresenter {

    boolean checkCanSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir);
}
