package com.wangdaye.mysplash._common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Ink page indicator behavior.
 * */

public class InkPageIndicatorBehavior<V extends InkPageIndicator> extends CoordinatorLayout.Behavior<V> {

    public InkPageIndicatorBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        int marginTop = (int) new DisplayUtils(parent.getContext()).dpToPx(16);
        int statusBarHeight = DisplayUtils.getStatusBarHeight(parent.getContext().getResources());
        child.layout(
                (int) (0.5 * (parent.getMeasuredWidth() - child.getMeasuredWidth())),
                marginTop + statusBarHeight,
                (int) (0.5 * (parent.getMeasuredWidth() + child.getMeasuredWidth())),
                marginTop + statusBarHeight + child.getMeasuredHeight());
        return true;
    }
}
