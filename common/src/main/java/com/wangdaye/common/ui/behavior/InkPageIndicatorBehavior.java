package com.wangdaye.common.ui.behavior;

import android.content.Context;
import android.util.AttributeSet;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.ui.widget.AutoHideInkPageIndicator;

import org.jetbrains.annotations.NotNull;

/**
 * Ink page indicator behavior.
 *
 * Behavior class for {@link AutoHideInkPageIndicator},
 * it's used to help {@link CoordinatorLayout} to layout target child view.
 *
 * */

public class InkPageIndicatorBehavior<V extends InkPageIndicator> extends CoordinatorLayout.Behavior<V> {

    public InkPageIndicatorBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NotNull CoordinatorLayout parent, @NotNull V child, int layoutDirection) {
        int marginTop = parent.getResources().getDimensionPixelSize(R.dimen.normal_margin);
        int statusBarHeight = MysplashApplication.getInstance().getWindowInsets().top;
        child.layout(
                (int) (0.5 * (parent.getMeasuredWidth() - child.getMeasuredWidth())),
                marginTop + statusBarHeight,
                (int) (0.5 * (parent.getMeasuredWidth() + child.getMeasuredWidth())),
                marginTop + statusBarHeight + child.getMeasuredHeight()
        );
        return true;
    }
}
