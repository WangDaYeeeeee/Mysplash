package com.wangdaye.mysplash.common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Preview icon behavior.
 *
 * Behavior class for icon container view in
 * {@link com.wangdaye.mysplash.common.ui.activity.PreviewActivity}, it's used to help the
 * {@link CoordinatorLayout} to layout target child view.
 *
 * */

public class PreviewIconBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    /** <br> life cycle. */

    public PreviewIconBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> UI. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        child.layout(
                0,
                parent.getMeasuredHeight(),
                child.getMeasuredWidth(),
                parent.getMeasuredHeight() + child.getMeasuredHeight());
        return true;
    }
}
