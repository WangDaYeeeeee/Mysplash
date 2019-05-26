package com.wangdaye.mysplash.common.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;

import com.pixelcan.inkpageindicator.InkPageIndicator;

/**
 * Auto hide ink page indicator.
 *
 * A {@link InkPageIndicator} that can hide itself automatically.
 *
 * */

public class AutoHideInkPageIndicator extends InkPageIndicator {

    private ObjectAnimator showAnimator;
    private ObjectAnimator dismissAnimator;

    public AutoHideInkPageIndicator(Context context) {
        super(context);
        this.init();
    }

    public AutoHideInkPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AutoHideInkPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init() {
        showAnimator = ObjectAnimator.ofFloat(
                this, "alpha", getAlpha(), 0.7f
        ).setDuration(100);

        dismissAnimator = ObjectAnimator.ofFloat(
                this, "alpha", getAlpha(), 0
        ).setDuration(200);
        dismissAnimator.setStartDelay(600);
    }

    public void setDisplayState(boolean show) {
        dismissAnimator.cancel();

        if (show) {
            showAnimator.cancel();
            showAnimator.setFloatValues(getAlpha(), 0.7f);
            showAnimator.start();
        } else {
            dismissAnimator.setFloatValues(getAlpha(), 0);
            dismissAnimator.start();
        }
    }
}
