package com.wangdaye.mysplash._common.utils.widget.glide;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.request.animation.ViewPropertyAnimation;

/**
 * Fade animator.
 * */

public class FadeAnimator implements ViewPropertyAnimation.Animator {

    @Override
    public void animate(View view) {
        if (!view.isEnabled()) {
            view.setEnabled(true);
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            animator.setDuration(300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }
}
