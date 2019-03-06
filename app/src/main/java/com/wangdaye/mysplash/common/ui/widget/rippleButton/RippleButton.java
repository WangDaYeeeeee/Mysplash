package com.wangdaye.mysplash.common.ui.widget.rippleButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
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
 * */

public class RippleButton extends CardView
        implements View.OnClickListener {

    @BindView(R.id.container_ripple_button) RelativeLayout container;
    @BindView(R.id.container_ripple_button_text) TextView text;
    @BindView(R.id.container_ripple_button_ripple) RippleView ripple;
    @BindView(R.id.container_ripple_button_progress) CircularProgressView progress;

    private ProgressAlphaAnimation progressAlphaAnimation;
    private OnSwitchListener listener;

    private State state;
    public enum State {
        OFF, TRANSFORM_TO_ON, ON, TRANSFORM_TO_OFF
    }

    private String titleOff;
    private String titleOn;

    @ColorInt private int colorLight;
    @ColorInt private int colorDark;

    private int fingerX, fingerY;

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
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.container_ripple_button, this, false);
        addView(v);
        ButterKnife.bind(this, this);
        initData(attrs, defStyleAttr);
        initWidget();
    }

    private void initData(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RippleButton, defStyleAttr, 0);
        String titleOn = a.getString(R.styleable.RippleButton_rb_title_on);
        String titleOff = a.getString(R.styleable.RippleButton_rb_title_off);
        a.recycle();

        this.titleOff = TextUtils.isEmpty(titleOff) ? "OFF" : titleOff;
        this.titleOn = TextUtils.isEmpty(titleOn) ? "ON" : titleOn;

        if (ThemeManager.getInstance(getContext()).isLightTheme()) {
            this.colorLight = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_light);
            this.colorDark = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_dark);
        } else {
            this.colorLight = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_dark);
            this.colorDark = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark_light);
        }
    }

    private void initWidget() {
        setOnClickListener(this);
        setPreventCornerOverlap(false);

        setCardElevation(0);

        rippleAlphaShow.setDuration(100);
        rippleAlphaShow.setInterpolator(new AccelerateDecelerateInterpolator());
        rippleAlphaHide.setDuration(200);
        rippleAlphaHide.setInterpolator(new AccelerateDecelerateInterpolator());

        setState(State.ON);
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

        fingerX = getMeasuredWidth() / 2;
        fingerY = getMeasuredHeight() / 2;
    }

    // touch.

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (state == State.ON || state == State.OFF || listener != null) {
                    fingerX = (int) event.getX();
                    fingerY = (int) event.getY();
                    listener.onSwitch(state);
                }
                break;
        }
        return true;
    }

    // control.

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            controlColor(state);
            controlTitle(state);
            controlAnimation(state);
        }
    }

    private void controlColor(State state) {
        switch (state) {
            case OFF:
                text.setTextColor(colorDark);
                progress.setColor(colorDark);
                ripple.setColor(colorLight);
                setCardBackgroundColor(colorLight);
                break;

            case TRANSFORM_TO_ON:
                text.setTextColor(colorDark);
                progress.setColor(colorLight);
                ripple.setColor(colorDark);
                setCardBackgroundColor(colorLight);
                break;

            case ON:
                text.setTextColor(colorLight);
                progress.setColor(colorLight);
                ripple.setColor(colorDark);
                setCardBackgroundColor(colorDark);
                break;

            case TRANSFORM_TO_OFF:
                text.setTextColor(colorLight);
                progress.setColor(colorDark);
                ripple.setColor(colorLight);
                setCardBackgroundColor(colorDark);
                break;
        }
    }

    private void controlTitle(State state) {
        switch (state) {
            case OFF:
            case TRANSFORM_TO_ON:
                text.setText(titleOff);
                break;

            case ON:
            case TRANSFORM_TO_OFF:
                text.setText(titleOn);
                break;
        }
    }

    private void controlAnimation(State state) {
        switch (state) {
            case OFF:
            case ON:
                // ripple fade out.
                rippleAlphaShow.cancel();
                rippleAlphaHide.cancel();
                rippleAlphaHide.reset();
                startAnimation(rippleAlphaHide);

                // progress fade out.
                doProgressAnimation(progress.getAlpha(), 0);
                break;

            case TRANSFORM_TO_ON:
            case TRANSFORM_TO_OFF:
                // ripple fade in.
                rippleAlphaShow.cancel();
                rippleAlphaHide.cancel();
                rippleAlphaShow.reset();
                startAnimation(rippleAlphaShow);
                ripple.drawRipple(fingerX, fingerY);
                fingerX = getMeasuredWidth();
                fingerY = getMeasuredHeight();

                // progress fade in.
                doProgressAnimation(progress.getAlpha(), 1);
                break;
        }
    }

    public void setButtonTitles(String off, String on) {
        titleOff = off;
        titleOn = on;
        controlTitle(state);
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

    // interface.

    // on switch swipeListener.

    public interface OnSwitchListener {

        void onSwitch(State current);
    }

    public void setOnSwitchListener(OnSwitchListener l) {
        this.listener = l;
    }

    // on click listener.

    @Override
    public void onClick(View view) {
        // do nothing.
    }
}
