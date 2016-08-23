package com.wangdaye.mysplash._common.ui.behavior;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash._common.ui.widget.FreedomImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Overlap app bar behavior.
 * */

public class OverlapAppBarBehavior<V extends AppBarLayout> extends CoordinatorLayout.Behavior<V> {
    // widget
    private V child;
    private DisplayUtils utils;

    // data
    private float scrollTrigger = 0;
    private boolean showing = false;

    /** <br> life cycle. */

    public OverlapAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.utils = new DisplayUtils(context);
    }

    /** <br> depend. */

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        if (dependency instanceof FreedomImageView) {
            this.child = child;
            scrollTrigger = dependency.getMeasuredHeight()
                    - DisplayUtils.getStatusBarHeight(parent.getContext().getResources());
            return true;
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        float scrollDistance = (float) (-dependency.getY() * 2.0);
        setChildVisibility((int) scrollDistance);
        return true;
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        child.layout(
                0,
                (int) (-child.getMeasuredHeight() - utils.dpToPx(8)),
                child.getMeasuredWidth(),
                0);
        return true;
    }

    private void setChildVisibility(int scrollY) {
        if (scrollY >= scrollTrigger && !showing) {
            animShow();
        } else if (scrollY < scrollTrigger && showing) {
            animHide();
        }
    }

    private void animShow() {
        showing = true;
        ObjectAnimator
                .ofFloat(child, "translationY", 0, child.getMeasuredHeight() + utils.dpToPx(8))
                .setDuration(300)
                .start();
    }

    private void animHide() {
        showing = false;
        ObjectAnimator
                .ofFloat(child, "translationY", child.getMeasuredHeight() + utils.dpToPx(8), 0)
                .setDuration(300)
                .start();
    }
}
