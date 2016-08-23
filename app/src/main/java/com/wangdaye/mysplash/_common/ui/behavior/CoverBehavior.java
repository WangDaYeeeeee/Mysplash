package com.wangdaye.mysplash._common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash._common.ui.widget.FreedomImageView;

/**
 * Cover behavior.
 * */

public class CoverBehavior<V extends FreedomImageView> extends CoordinatorLayout.Behavior<V>
        implements NestedScrollView.OnScrollChangeListener {
    // widget
    private CoordinatorLayout parent;
    private V child;

    // data
    private float top = 0;

    /** <br> life cycle. */

    public CoverBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> depend. */

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        if (dependency instanceof NestedScrollView) {
            ((NestedScrollView) dependency).setOnScrollChangeListener(this);
            this.parent = parent;
            this.child = child;
        }
        return false;
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        int startX = (int) (-(child.getMeasuredWidth() - parent.getMeasuredWidth()) / 2.0);
        child.layout(
                startX,
                (int) top,
                startX + child.getMeasuredWidth(),
                (int) (top + child.getMeasuredHeight()));
        return true;
    }

    private void setChildTop(CoordinatorLayout parent, V child, float dy) {
        dy = (float) (-dy / 2.0);
        top += dy;
        top = (float) Math.max(-child.getMeasuredHeight() / 2.0, top);
        top = Math.min(0, top);
        parent.requestLayout();
    }

    /** <br> interface. */

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        setChildTop(parent, child, scrollY - oldScrollY);
    }
}
