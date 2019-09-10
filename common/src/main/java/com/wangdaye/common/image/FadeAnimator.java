package com.wangdaye.common.image;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.wangdaye.common.R;

/**
 * Fade animator.
 *
 * A fade animator for {@link com.bumptech.glide.Glide}.
 *
 * */

public class FadeAnimator implements ViewPropertyAnimation.Animator {

    @Override
    public void animate(View view) {
        Boolean fadeInFlag = (Boolean) view.getTag(R.id.tag_item_image_fade_in_flag);
        if (fadeInFlag == null || fadeInFlag) {
            view.setTag(R.id.tag_item_image_fade_in_flag, false);
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            animator.setDuration(300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        }
    }
}
