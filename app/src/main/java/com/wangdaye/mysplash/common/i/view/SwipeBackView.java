package com.wangdaye.mysplash.common.i.view;

import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;

/**
 * Swipe back view.
 *
 * A view which can interact with {@link SwipeBackManageView}. By implementing this interface,
 * view can respond the operation from
 * {@link com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout}.
 *
 * */

public interface SwipeBackView {

    boolean checkCanSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir);
}
