package com.wangdaye.mysplash.common.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Photo fab behavior.
 * */

public class PhotoFabBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    /** <br> life cycle. */

    public PhotoFabBehavior() {
        super();
    }

    public PhotoFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> depend. */

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        View cover = parent.getChildAt(0);
        DisplayUtils utils = new DisplayUtils(parent.getContext());

        NestedScrollView v= (NestedScrollView) dependency;
        child.setY(cover.getMeasuredHeight() - utils.dpToPx((int) (56 / 2.0)) - v.getScrollY());
        return true;
    }
}
