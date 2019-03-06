package com.wangdaye.mysplash.common.ui.widget;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.image.ImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Circular progress button.
 *
 * A ImageButton that has control state operation.
 *
 * */

public class CircularProgressIcon extends FrameLayout {

    @BindView(R.id.container_circular_progress_icon_image)
    AppCompatImageView image;

    @BindView(R.id.container_circular_progress_icon_progress)
    CircularProgressView progress;

    private ShowAnimation showAnimation;
    private HideAnimation hideAnimation;

    private boolean animating;

    @StateRule
    private int state;

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
        initWidget();
        forceSetResultState(android.R.color.transparent);
    }

    private void initWidget() {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.container_circular_progress_icon, this, false);
        addView(v);
        ButterKnife.bind(this, this);
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

    public void setState(@StateRule int state) {
        this.state = state;
    }

    // anim.

    public void setProgressState() {
        if (getState() == STATE_RESULT) {
            setState(STATE_PROGRESS);
            cancelAllAnimation();
            showAnimation = new ShowAnimation(progress);
            progress.startAnimation(showAnimation);
            hideAnimation = new HideAnimation(image);
            image.startAnimation(hideAnimation);
        }
        // else forceSetProgressState();
    }

    public void setResultState(@DrawableRes int imageId) {
        if (getState() == STATE_PROGRESS) {
            setState(STATE_RESULT);
            cancelAllAnimation();
            ImageHelper.loadResourceImage(getContext(), image, imageId);
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

    private void forceSetProgressState() {
        cancelAllAnimation();
        setState(STATE_PROGRESS);
        setAnimating(false);

        image.setAlpha(0f);
        image.setScaleX(1f);
        image.setScaleY(1f);
        image.setRotation(0);

        progress.setAlpha(1f);
        progress.setScaleX(1f);
        progress.setScaleY(1f);
        progress.setRotation(0);
    }

    private void forceSetResultState(@DrawableRes int imageId) {
        cancelAllAnimation();
        setState(STATE_RESULT);
        setAnimating(false);
        ImageHelper.loadResourceImage(getContext(), image, imageId);

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

    // image.

    public void recycleImageView() {
        ImageHelper.releaseImageView(image);
    }
}
