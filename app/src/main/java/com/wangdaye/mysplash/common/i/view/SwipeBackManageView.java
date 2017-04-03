package com.wangdaye.mysplash.common.i.view;

import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;

/**
 * Swipe back manage view.
 *
 * A view which can control {@link SwipeBackView} to respond the operation from
 * {@link com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout}.
 *
 * */

public interface SwipeBackManageView {

    boolean checkCanSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir);
}
