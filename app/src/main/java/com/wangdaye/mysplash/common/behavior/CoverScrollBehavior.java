package com.wangdaye.mysplash.common.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.widget.FreedomImageView;

/**
 * Cover scroll behavior.
 * */

public class CoverScrollBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    // data
    private float top = 0;

    /** <br> life cycle. */

    public CoverScrollBehavior() {
        super();
    }

    public CoverScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> measure. */

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, V child,
                                  int parentWidthMeasureSpec, int widthUsed,
                                  int parentHeightMeasureSpec, int heightUsed) {
        DisplayUtils utils = new DisplayUtils(parent.getContext());
        FreedomImageView v = (FreedomImageView) child;
        float height = parent.getResources().getDisplayMetrics().heightPixels - utils.dpToPx(260);
        float width = height / v.getPhotoHeight() * v.getPhotoWidth();

        if (width < parent.getResources().getDisplayMetrics().widthPixels) {
            int w = parent.getResources().getDisplayMetrics().widthPixels;
            child.measure(
                    View.MeasureSpec.makeMeasureSpec(
                            w,
                            View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            (int) (w / v.getPhotoWidth() * v.getPhotoHeight()),
                            View.MeasureSpec.EXACTLY));
        } else {
            child.measure(
                    View.MeasureSpec.makeMeasureSpec(
                            (int) width,
                            View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            (int) height,
                            View.MeasureSpec.EXACTLY));
        }
        return true;
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        DisplayUtils utils = new DisplayUtils(parent.getContext());
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        int spaceHeight = (int) (parent.getMeasuredHeight() - utils.dpToPx(260));

        if (width > parent.getMeasuredWidth()) {
            child.layout(
                    (int) (-(width - parent.getMeasuredWidth()) / 2.0),
                    0,
                    (int) (-(width - parent.getMeasuredWidth()) / 2.0) + child.getMeasuredWidth(),
                    child.getMeasuredHeight());
        } else {
            child.layout(
                    0,
                    (int) (-(height - spaceHeight) / 2.0),
                    child.getMeasuredWidth(),
                    (int) (-(height - spaceHeight) / 2.0) + child.getMeasuredHeight());
        }
        return true;
    }

    /** <br> depend. */

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        NestedScrollView v= (NestedScrollView) dependency;
        child.setY((float) (-v.getScrollY() / 2.0));
        return true;
    }

    /** <br> nested scroll. */

    /*
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       V child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                  int dx, int dy, int[] consumed) {
        setChildOffset(child, dy);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target,
                                    float velocityX, float velocityY) {
        int dy = (int) (velocityY / 1000.0 * 16);
        setChildOffset(child, dy);
        return false;
    }

    private void setChildOffset(V child, int dy) {
        float newTop;
        newTop = (float) Math.min(0, top - dy / 2.0); // top <= 0
        newTop = (float) Math.max(-child.getMeasuredHeight() / 2.0, top - dy / 2.0); // top >= -child.height * 0.5
        if (newTop != top) {
            child.offsetTopAndBottom((int) (newTop - top));
            top = newTop;
        }
    }
    */
}
