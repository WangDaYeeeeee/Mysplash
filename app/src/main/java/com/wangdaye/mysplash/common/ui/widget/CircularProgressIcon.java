package com.wangdaye.mysplash.common.ui.widget;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Circular progress button.
 *
 * A ImageButton that has control state operation.
 *
 * */

public class CircularProgressIcon extends FrameLayout {

    private AppCompatImageView image;
    private CircularProgressView progress;

    @Nullable private ShowAnimation showAnimation;
    @Nullable private HideAnimation hideAnimation;

    private boolean animating;

    @StateRule private int state;

    public static final int STATE_PROGRESS = -1;
    public static final int STATE_RESULT = 1;
    @IntDef({STATE_PROGRESS, STATE_RESULT})
    private @interface StateRule {}

    private class ShowAnimation extends Animation {

        private View target;

        ShowAnimation(View target) {
            this.target = target;
            setDuration(150);
            setInterpolator(new AccelerateDecelerateInterpolator());
            setAnimationListener(animationListener);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            target.setAlpha(interpolatedTime);
            target.setScaleX((float) (0.5 + 0.5 * interpolatedTime));
            target.setScaleY((float) (0.5 + 0.5 * interpolatedTime));
            target.setRotation(-90 + 90 * interpolatedTime);
        }
    }

    private class HideAnimation extends Animation {

        private View target;

        HideAnimation(View target) {
            this.target = target;
            setDuration(150);
            setInterpolator(new AccelerateDecelerateInterpolator());
            setAnimationListener(animationListener);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            target.setAlpha(1 - interpolatedTime);
            target.setScaleX((float) (1 - 0.5 * interpolatedTime));
            target.setScaleY((float) (1 - 0.5 * interpolatedTime));
        }
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            setAnimating(true);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setAnimating(false);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    };

    public CircularProgressIcon(Context context) {
        super(context);
        this.initialize();
    }

    public CircularProgressIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CircularProgressIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        image = new AppCompatImageView(getContext());
        image.setBackgroundColor(Color.TRANSPARENT);
        CircularProgressIcon.LayoutParams imageParams = new CircularProgressIcon.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        int imageMargin = getResources().getDimensionPixelSize(R.dimen.little_margin);
        imageParams.setMargins(imageMargin, imageMargin, imageMargin, imageMargin);
        image.setLayoutParams(imageParams);
        addView(image);

        progress = new CircularProgressView(getContext());
        progress.setIndeterminate(true);
        progress.setColor(Color.WHITE);
        CircularProgressIcon.LayoutParams progressParams = new CircularProgressIcon.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        int progressMargin = (int) new DisplayUtils(getContext()).dpToPx(5);
        progressParams.setMargins(progressMargin, progressMargin, progressMargin, progressMargin);
        progress.setLayoutParams(progressParams);
        addView(progress);

        forceSetResultState(android.R.color.transparent);
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (state == STATE_PROGRESS) {
            progress.startAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        progress.stopAnimation();
    }

    // control.

    /**
     * Whether the button is free.
     * */
    public boolean isUsable() {
        return getState() == STATE_RESULT && !isAnimating();
    }

    public void setProgressColor(@ColorInt int color) {
        progress.setColor(color);
    }

    @StateRule
    public int getState() {
        return state;
    }

    // anim.

    public void setProgressState() {
        if (state == STATE_RESULT) {
            state = STATE_PROGRESS;

            cancelAllAnimation();
            progress.startAnimation();

            showAnimation = new ShowAnimation(progress);
            progress.startAnimation(showAnimation);

            hideAnimation = new HideAnimation(image);
            image.startAnimation(hideAnimation);
        }
    }

    public void setResultState(@DrawableRes @XmlRes int imageId) {
        if (state == STATE_PROGRESS) {
            state = STATE_RESULT;

            cancelAllAnimation();
            progress.stopAnimation();
            image.setImageResource(imageId);

            showAnimation = new ShowAnimation(image);
            image.startAnimation(showAnimation);

            hideAnimation = new HideAnimation(progress);
            progress.startAnimation(hideAnimation);
        } else {
            forceSetResultState(imageId);
        }
    }

    private void cancelAllAnimation() {
        if (showAnimation != null) {
            showAnimation.cancel();
        }
        if (hideAnimation != null) {
            hideAnimation.cancel();
        }
    }

    // force.

    private void forceSetResultState(@DrawableRes @XmlRes int imageId) {
        state = STATE_RESULT;

        cancelAllAnimation();
        setAnimating(false);

        image.setImageResource(imageId);

        image.setAlpha(1f);
        image.setScaleX(1f);
        image.setScaleY(1f);
        image.setRotation(0);

        progress.setAlpha(0f);
        progress.setScaleX(1f);
        progress.setScaleY(1f);
        progress.setRotation(0);
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }
}
