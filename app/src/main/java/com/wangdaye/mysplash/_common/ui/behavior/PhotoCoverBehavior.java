package com.wangdaye.mysplash._common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Cover behavior.
 * */

public class PhotoCoverBehavior<V extends FreedomImageView> extends CoordinatorLayout.Behavior<V>
        implements NestedScrollView.OnScrollChangeListener {
    // widget
    private CoordinatorLayout parent;
    private V child;
    private AlphaAnimation animation;

    // data
    private int statusBarHeight;

    /** <br> life cycle. */

    public PhotoCoverBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.statusBarHeight = DisplayUtils.getStatusBarHeight(context.getResources());
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
        child.layout(
                0,
                0,
                child.getMeasuredWidth(),
                child.getMeasuredHeight());
        return true;
    }

    private void setChildTop(V child, int scrollY) {
        float top = (float) (-scrollY * 0.5);
        child.setY(top);
    }

    /** <br> interface. */

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        setChildTop(child, scrollY);
        if (oldScrollY < child.getMeasuredHeight() - statusBarHeight
                && scrollY >= child.getMeasuredHeight() - statusBarHeight) {
            animChangeStatusBarAlpha(
                    parent.getChildAt(1),
                    parent.getChildAt(1).getAlpha(),
                    1);
        } else if (oldScrollY >= child.getMeasuredHeight() - statusBarHeight
                && scrollY < child.getMeasuredHeight() - statusBarHeight) {
            animChangeStatusBarAlpha(
                    parent.getChildAt(1),
                    parent.getChildAt(1).getAlpha(),
                    0);
        }
    }

    /** <br> UI. */

    private void animChangeStatusBarAlpha(View v, float from, float to) {
        if (animation != null) {
            animation.cancel();
        }
        animation = new AlphaAnimation(v, from, to);
        animation.setDuration(300);
        animation.setFillAfter(true);

        v.startAnimation(animation);
    }

    /** <br> inner class. */

    private class AlphaAnimation extends Animation {
        // widget
        private View v;

        // data
        private float from;
        private float to;

        // life cycle.
        AlphaAnimation(View v, float from, float to) {
            this.v = v;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            v.setAlpha(from + interpolatedTime * (to - from));
        }
    }
}
