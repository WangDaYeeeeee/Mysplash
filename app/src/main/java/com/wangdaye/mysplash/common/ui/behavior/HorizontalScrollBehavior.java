package com.wangdaye.mysplash.common.ui.behavior;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.common.ui.widget.photoView.PhotoView;

/**
 * Horizontal scroll behavior.
 * */

public class HorizontalScrollBehavior<V extends PhotoView> extends CoordinatorLayout.Behavior<V> {

    public HorizontalScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, V child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child,
                                  View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                               View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(
                coordinatorLayout, child, target,
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        child.setTranslate(dxConsumed, dyConsumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                   View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }
}
