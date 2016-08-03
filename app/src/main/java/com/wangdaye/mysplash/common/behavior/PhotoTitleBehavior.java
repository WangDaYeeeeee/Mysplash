package com.wangdaye.mysplash.common.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Photo title behavior.
 * */

public class PhotoTitleBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    /** <br> life cycle. */

    public PhotoTitleBehavior() {
        super();
    }

    public PhotoTitleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> depend. */

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        NestedScrollView v= (NestedScrollView) dependency;
        child.setY(parent.getChildAt(0).getMeasuredHeight() - child.getMeasuredHeight() - v.getScrollY());
        return true;
    }
}
