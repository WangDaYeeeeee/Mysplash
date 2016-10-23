package com.wangdaye.mysplash._common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Photo scroll view behavior.
 * */

public class PhotoScrollViewBehavior<V extends NestedScrollView> extends CoordinatorLayout.Behavior<V> {
    // data
    private boolean haveSetPadding = false;

    /** <br> life cycle. */

    public PhotoScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        if (!haveSetPadding) {
            haveSetPadding = true;
            child.setPadding(0, DisplayUtils.getStatusBarHeight(parent.getResources()), 0, 0);
        }
        return false;
    }
}
