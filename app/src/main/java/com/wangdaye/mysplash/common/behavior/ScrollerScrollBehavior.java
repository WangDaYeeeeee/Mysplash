package com.wangdaye.mysplash.common.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Scroller scroll behavior.
 * */

public class ScrollerScrollBehavior<V extends NestedScrollView> extends CoordinatorLayout.Behavior<V>{

    /** <br> life cycle. */

    public ScrollerScrollBehavior() {
        super();
    }

    public ScrollerScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        DisplayUtils utils = new DisplayUtils(parent.getContext());
        float height = parent.getResources().getDisplayMetrics().heightPixels - utils.dpToPx(260);
        child.setPadding(0, (int) height, 0, 0);
        child.setClipToPadding(false);
        child.layout(0, 0, parent.getMeasuredWidth(), parent.getMeasuredHeight());
        return true;
    }
}