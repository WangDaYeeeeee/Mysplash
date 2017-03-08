package com.wangdaye.mysplash._common.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;

/**
 * Circular progress button.
 * */

public class CircularProgressIcon extends FrameLayout {
    // widget
    private ImageView image;
    private CircularProgressView progress;

    private ShowAnimation showAnimation;
    private HideAnimation hideAnimation;

    // data
    private boolean animating;
    private int state;

    private static final int STATE_PROGRESS = -1;
    private static final int STATE_RESULT = 1;

    /** <br> life cycle. */

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircularProgressIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        initWidget();
        forceSetResultState(android.R.color.transparent);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.container_circular_progress_icon, this, false);
        addView(v);
        this.image = (ImageView) v.findViewById(R.id.container_circular_progress_icon_image);
        this.progress = (CircularProgressView) v.findViewById(R.id.container_circular_progress_icon_progress);
    }

    public void forceSetProgressState() {
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

    public void forceSetResultState(@DrawableRes int imageId) {
        cancelAllAnimation();
        setState(STATE_RESULT);
        setAnimating(false);
        ImageHelper.loadIcon(getContext(), image, imageId);

        image.setAlpha(1f);
        image.setScaleX(1f);
        image.setScaleY(1f);
        image.setRotation(0);

        progress.setAlpha(0f);
        progress.setScaleX(1f);
        progress.setScaleY(1f);
        progress.setRotation(0);
    }

    public void setProgressState() {
        if (getState() == STATE_RESULT) {
            setState(STATE_PROGRESS);
            cancelAllAnimation();
            showAnimation = new ShowAnimation(progress);
            progress.startAnimation(showAnimation);
            hideAnimation = new HideAnimation(image);
            image.startAnimation(hideAnimation);
        }
    }

    public void setResultState(@DrawableRes int imageId) {
        if (getState() == STATE_PROGRESS) {
            setState(STATE_RESULT);
            cancelAllAnimation();
            ImageHelper.loadIcon(getContext(), image, imageId);
            showAnimation = new ShowAnimation(image);
            image.startAnimation(showAnimation);
            hideAnimation = new HideAnimation(progress);
            progress.startAnimation(hideAnimation);
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

    public void recycleImageView() {
        ImageHelper.releaseImageView(image);
    }

    /** <br> data. */

    public boolean isUsable() {
        return getState() == STATE_RESULT && !isAnimating();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    /** <br> inner class. */

    private class ShowAnimation extends Animation {
        // widget
        private View target;

        // life cycle.
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
        // widget
        private View target;

        // life cycle.
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
}
