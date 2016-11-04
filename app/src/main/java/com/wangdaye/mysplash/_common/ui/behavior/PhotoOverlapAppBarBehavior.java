package com.wangdaye.mysplash._common.ui.behavior;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Overlap app bar behavior.
 * */

public class PhotoOverlapAppBarBehavior<V extends LinearLayout> extends CoordinatorLayout.Behavior<V> {
    // data
    private float scrollTrigger = 0;
    private boolean showing = false;

    /** <br> life cycle. */

    public PhotoOverlapAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> depend. */

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        if (dependency instanceof FreedomImageView) {
            scrollTrigger = dependency.getMeasuredHeight()
                    - DisplayUtils.getStatusBarHeight(parent.getContext().getResources());
            return true;
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
        float scrollY = (float) (-dependency.getY() * 2.0);
        if (scrollY >= scrollTrigger && !showing) {
            animShow(child);
        } else if (scrollY < scrollTrigger && showing) {
            animHide(child);
        }
        return true;
    }

    private void animShow(V child) {
        showing = true;
        child.getChildAt(1).setVisibility(View.VISIBLE);

        View v = child.getChildAt(0);
        v.clearAnimation();
        ObjectAnimator
                .ofFloat(v, "alpha", v.getAlpha(), 1)
                .setDuration(300)
                .start();
    }

    private void animHide(V child) {
        showing = false;
        child.getChildAt(1).setVisibility(View.GONE);

        View v = child.getChildAt(0);
        v.clearAnimation();
        ObjectAnimator
                .ofFloat(v, "alpha", v.getAlpha(), 0)
                .setDuration(300)
                .start();
    }
}
