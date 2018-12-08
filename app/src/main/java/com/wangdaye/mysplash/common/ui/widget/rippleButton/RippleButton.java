package com.wangdaye.mysplash.common.ui.widget.rippleButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Ripple button.
 *
 * A Button widget that has reveal animation and control state operation.
 *
 * The process of transform state is as follow:
 *
 * 0.     {@link MotionEvent#ACTION_UP}.
 * 1.     execute reveal animation in {@link RippleView} & asynchronous task start.
 *
 * 2-1-0. task complete.
 * 2-1-1. {@link #switchUI()} & execute fade animation to hide {@link RippleView}.
 *
 * 2-2-0. reveal animation complete.
 * 2-2-1. execute fade animation to show {@link CircularProgressView}.
 * 2-2-3. task complete --> {@link #switchUI()} & execute fade animation to hide
 *        {@link CircularProgressView} and {@link RippleView}.
 *
 * */

public class RippleButton extends CardView
        implements View.OnClickListener, RippleView.RippleAnimatingCallback {

    @BindView(R.id.container_ripple_button)
    RelativeLayout container;

    @BindView(R.id.container_ripple_button_text)
    TextView text;

    @BindView(R.id.container_ripple_button_ripple)
    RippleView ripple;

    @BindView(R.id.container_ripple_button_progress)
    CircularProgressView progress;

    private ProgressAlphaAnimation progressAlphaAnimation;
    private OnSwitchListener listener;

    // if set true, there will has no difference between this view and a Button.
    private boolean dontAnimate;

    private boolean animating;
    private boolean switchOn;

    // When the view is performing an asynchronous task, there will be 2 cases.
    // 1. If the transform animation complete first, the animation callback will set
    // waitingResponse to true and wait for the asynchronous task complete.
    // 2. If the asynchronous task complete first, the situation will turn.
    private boolean waitingResponse;
    private boolean waitingAnimation;

    private boolean switchSucceed; // save the result of the asynchronous task.

    @Size(2)
    private String[] buttonTitles;
    @Size(2)
    private int[] backgroundColors;
    @Size(2)
    private int[] widgetColors;

    private Animation rippleAlphaShow = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            ripple.setAlpha((float) (0.5 + 0.5 * interpolatedTime));
        }
    };

    private Animation rippleAlphaHide = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            ripple.setAlpha(1 - interpolatedTime);
        }
    };

    private class ProgressAlphaAnimation extends Animation {

        private float from;
        private float to;

        ProgressAlphaAnimation(float from, float to) {
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            progress.setAlpha(from + (to - from) * interpolatedTime);
        }
    }

    public RippleButton(Context context) {
        super(context);
        this.initialize(null, 0);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(attrs, 0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(attrs, defStyleAttr);
    }

    private void initialize(AttributeSet attrs, int defStyleAttr) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.container_ripple_button, this, false);
        addView(v);
        ButterKnife.bind(this, this);
        initData(attrs, defStyleAttr);
        initWidget();
    }

    private void initData(AttributeSet attrs, int defStyleAttr) {
        setSwitchOn(false);
        setAnimating(false);

        if (ThemeManager.getInstance(getContext()).isLightTheme()) {
            backgroundColors = new int[] {
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_light),
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_dark)};
            widgetColors = new int[] {
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_dark),
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_light)};
        } else {
            backgroundColors = new int[] {
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_dark),
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_light)};
            widgetColors = new int[] {
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_light),
                    ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_dark)};
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RippleButton, defStyleAttr, 0);
        String titleOn = a.getString(R.styleable.RippleButton_rb_title_on);
        String titleOff = a.getString(R.styleable.RippleButton_rb_title_off);
        a.recycle();

        this.buttonTitles = new String[] {
                TextUtils.isEmpty(titleOff) ? "OFF" : titleOff,
                TextUtils.isEmpty(titleOn) ? "ON" : titleOn};
    }

    private void initWidget() {
        setOnClickListener(this);
        setPreventCornerOverlap(false);

        setCardElevation(0);

        ripple.setRippleAnimatingCallback(this);

        forceSwitch(isSwitchOn());

        rippleAlphaShow.setDuration(100);
        rippleAlphaShow.setInterpolator(new AccelerateDecelerateInterpolator());
        rippleAlphaHide.setDuration(200);
        rippleAlphaHide.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    // draw.

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        container.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));

        // width.
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                // do nothing.
                break;

            case MeasureSpec.AT_MOST:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        Math.min(container.getMeasuredWidth(), width),
                        MeasureSpec.EXACTLY);
                break;

            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        container.getMeasuredWidth(),
                        MeasureSpec.EXACTLY);
                break;
        }

        // height.
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                // do nothing.
                break;

            case MeasureSpec.AT_MOST:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        Math.min(container.getMeasuredHeight(), height),
                        MeasureSpec.EXACTLY);
                break;

            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        container.getMeasuredHeight(),
                        MeasureSpec.EXACTLY);
                break;
        }

        container.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));

        setRadius((float) (MeasureSpec.getSize(heightMeasureSpec) * 0.5));
    }

    // touch.

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    if (isDontAnimate()) {
                        if (listener != null) {
                            listener.onSwitch(!isSwitchOn());
                        }
                    } else {
                        animSwitch((int) event.getX(), (int) event.getY());
                    }
                }
                break;
        }
        return true;
    }

    // control.

    // interface.

    private void switchUI() {
        setSwitchOn(!isSwitchOn());
        setCardBackgroundColor(backgroundColors[isSwitchOn() ? 1 : 0]);
        text.setTextColor(widgetColors[isSwitchOn() ? 1 : 0]);
        text.setText(buttonTitles[isSwitchOn() ? 1 : 0]);
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setSwitchOn(boolean on) {
        this.switchOn = on;
    }

    public void setButtonTitles(String[] titles) {
        if (titles != null && titles.length >= 2) {
            buttonTitles = titles;
            text.setText(buttonTitles[isSwitchOn() ? 1 : 0]);
        }
    }

    // force.

    public void forceSwitch(boolean switchTo) {
        setSwitchOn(switchTo);
        setAnimating(false);
        setCardBackgroundColor(backgroundColors[isSwitchOn() ? 1 : 0]);
        text.setTextColor(widgetColors[isSwitchOn() ? 1 : 0]);
        text.setText(buttonTitles[isSwitchOn() ? 1 : 0]);
        ripple.setAlpha(0);
        ripple.setColor(widgetColors[isSwitchOn() ? 0 : 1]);
        progress.setAlpha(0);
        progress.setColor(widgetColors[isSwitchOn() ? 0 : 1]);
    }

    public void forceProgress(boolean switchTo) {
        setSwitchOn(!switchTo);
        setAnimating(true);
        setWaitingResponse(true);
        setCardBackgroundColor(backgroundColors[isSwitchOn() ? 1 : 0]);
        text.setTextColor(widgetColors[isSwitchOn() ? 1 : 0]);
        text.setText(buttonTitles[isSwitchOn() ? 1 : 0]);
        ripple.setColor(widgetColors[isSwitchOn() ? 0 : 1]);
        ripple.setAlpha(1);
        progress.setColor(widgetColors[isSwitchOn() ? 0 : 1]);
        progress.setAlpha(1);
    }

    // anim transform.

    public void animSwitch(int x, int y) {
        if (!isAnimating() && !ripple.isDrawing()) {
            setAnimating(true);
            if (listener != null) {
                listener.onSwitch(!isSwitchOn());
            }
            // alpha.
            rippleAlphaShow.cancel();
            rippleAlphaShow.reset();
            startAnimation(rippleAlphaShow);
            // ripple.
            progress.setColor(widgetColors[isSwitchOn() ? 0 : 1]);
            ripple.drawRipple(backgroundColors[isSwitchOn() ? 0 : 1], x, y);
        }
    }

    private void doProgressAnimation(float from, float to) {
        if (progressAlphaAnimation != null) {
            progressAlphaAnimation.cancel();
        }
        progressAlphaAnimation = new ProgressAlphaAnimation(from, to);
        progressAlphaAnimation.setDuration(150);
        progressAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progress.startAnimation(progressAlphaAnimation);
    }

    public boolean isDontAnimate() {
        return dontAnimate;
    }

    public void setDontAnimate(boolean dontAnimate) {
        this.dontAnimate = dontAnimate;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        setWaitingResponse(false);
        setWaitingAnimation(false);
        setSwitchSucceed(false);
        this.animating = animating;
    }

    // callback.

    /**
     * A interface method for outside. When a asynchronous task complete, the callback need user
     * this method to set the result of task.
     *
     * @param succeed result of task.
     * */
    public void setSwitchResult(boolean succeed) {
        setSwitchSucceed(succeed);
        if (isWaitingResponse()) {
            // transform animation has already completed.
            if (isSwitchSucceed()) {
                switchUI();
            }
            if (progressAlphaAnimation != null) {
                progressAlphaAnimation.cancel();
            }
            progressAlphaAnimation = new ProgressAlphaAnimation(progress.getAlpha(), 0);
            progressAlphaAnimation.setDuration(150);
            progressAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            progress.startAnimation(progressAlphaAnimation);

            rippleAlphaHide.cancel();
            rippleAlphaHide.reset();
            rippleAlphaHide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // do nothing.
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setAnimating(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // do nothing.
                }
            });
            startAnimation(rippleAlphaHide);
        } else {
            // task complete but animation doesn't complete.
            setWaitingAnimation(true);
        }
    }

    public boolean isSwitchSucceed() {
        return switchSucceed;
    }

    public void setSwitchSucceed(boolean switchSucceed) {
        this.switchSucceed = switchSucceed;
    }

    public boolean isWaitingAnimation() {
        return waitingAnimation;
    }

    public void setWaitingAnimation(boolean waitingAnimation) {
        this.waitingAnimation = waitingAnimation;
    }

    public boolean isWaitingResponse() {
        return waitingResponse;
    }

    public void setWaitingResponse(boolean waitingResponse) {
        this.waitingResponse = waitingResponse;
    }

    // interface.

    // on switch swipeListener.

    public interface OnSwitchListener {
        void onSwitch(boolean switchTo);
    }

    public void setOnSwitchListener(OnSwitchListener l) {
        this.listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        // do nothing.
    }

    // ripple animating callback.

    @Override
    public void animationDone() {
        if (isWaitingAnimation()) {
            if (isSwitchSucceed()) {
                switchUI();
            }
            rippleAlphaHide.cancel();
            rippleAlphaHide.reset();
            rippleAlphaHide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // do nothing.
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setAnimating(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // do nothing.
                }
            });
            startAnimation(rippleAlphaHide);
        } else {
            setWaitingResponse(true);
            doProgressAnimation(0, 1);
        }
    }
}
