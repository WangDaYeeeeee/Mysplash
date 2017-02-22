package com.wangdaye.mysplash._common.utils.widget.glide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash._common.utils.AnimUtils;

/**
 * Photo request listener.
 * */

public abstract class ColorAnimRequestListener<T extends String, R extends GlideDrawable>
        implements RequestListener<T, R> {

    /** <br> UI. */

    protected void startColorAnimation(Context c, final ImageView target) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            target.setHasTransientState(true);
            final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
            final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                    matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
            saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                    () {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    // just animating the color matrix does not invalidate the
                    // drawable so need this update listener.  Also have to create a
                    // new CMCF as the matrix is immutable :(
                    target.setColorFilter(new ColorMatrixColorFilter(matrix));
                }
            });
            saturation.setDuration(2000L);
            saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(c));
            saturation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    target.clearColorFilter();
                    target.setHasTransientState(false);
                }
            });
            saturation.start();
        }
    }

    /** <br> interface. */

    @Override
    public boolean onException(Exception e, T model, Target<R> target, boolean isFirstResource) {
        // do nothing.
        return false;
    }
}
