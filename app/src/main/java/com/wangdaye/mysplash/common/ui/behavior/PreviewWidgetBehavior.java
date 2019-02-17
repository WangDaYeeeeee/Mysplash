package com.wangdaye.mysplash.common.ui.behavior;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Preview widget behavior.
 *
 * Behavior class for widget container view in
 * {@link com.wangdaye.mysplash.common.ui.activity.PreviewActivity}, it's used to help the
 * {@link CoordinatorLayout} to layout target child view.
 *
 * */

public class PreviewWidgetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    public PreviewWidgetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        child.layout(
                0,
                -child.getMeasuredHeight(),
                child.getMeasuredWidth(),
                0);
        return true;
    }
}