package com.wangdaye.mysplash.common.ui.widget.rippleButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Ripple button.
 * */
public class RippleButton extends CardView
        implements View.OnClickListener {

    private DisplayUtils utils;

    private TextView text;
    private RippleView ripple;
    private CircularProgressView progress;

    private ProgressAlphaAnimation progressAlphaAnimation;
    private OnSwitchListener listener;

    private State state;
    public enum State {
        OFF,
        TRANSFORM_TO_ON,
        ON,
        TRANSFORM_TO_OFF
    }

    private String titleOff;
    private String titleOn;

    @ColorInt private int colorLight;
    @ColorInt private int colorDark;

    private int fingerX, fingerY;

    private static final int TEXT_MARGIN_HORIZONTAL_DIP = 32;
    private static final int TEXT_MARGIN_VERTICAL_DIP = 8;

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
        utils = new DisplayUtils(getContext());

        text = new TextView(getContext());
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setGravity(Gravity.CENTER);
        text.setLines(1);
        RippleButton.LayoutParams textParams = new RippleButton.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.CENTER;
        text.setLayoutParams(textParams);
        addView(text);

        ripple = new RippleView(getContext());
        RippleButton.LayoutParams rippleParams = new RippleButton.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        rippleParams.gravity = Gravity.CENTER;
        ripple.setLayoutParams(rippleParams);
        addView(ripple);

        progress = new CircularProgressView(getContext());
        progress.setIndeterminate(true);
        progress.setColor(Color.DKGRAY);
        RippleButton.LayoutParams progressParams = new RippleButton.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.mini_icon_size),
                getResources().getDimensionPixelSize(R.dimen.mini_icon_size)
        );
        progressParams.gravity = Gravity.CENTER;
        progress.setLayoutParams(progressParams);
        addView(progress);

        initData(attrs, defStyleAttr);
        initWidget();
    }

    private void initData(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RippleButton, defStyleAttr, 0);
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
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int textMarginHorizontal = (int) utils.dpToPx(TEXT_MARGIN_HORIZONTAL_DIP);
        int textMarginVertical = (int) utils.dpToPx(TEXT_MARGIN_VERTICAL_DIP);

        text.measure(widthMeasureSpec, heightMeasureSpec);

        RippleButton.LayoutParams progressParams = (LayoutParams) progress.getLayoutParams();
        progress.measure(
                MeasureSpec.makeMeasureSpec(progressParams.width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(progressParams.height, MeasureSpec.EXACTLY)
        );

        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                width = Math.max(
                        text.getMeasuredWidth() + textMarginHorizontal * 2,
                        progress.getMeasuredWidth()
                );
                break;

            case MeasureSpec.EXACTLY:
                // do nothing.
                break;

            case MeasureSpec.AT_MOST:
                width = Math.min(
                        width,
                        Math.max(
                                text.getMeasuredWidth() + textMarginHorizontal * 2,
                                progress.getMeasuredWidth()
                        )
                );
                break;
        }
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                height = Math.max(
                        text.getMeasuredHeight() + textMarginVertical * 2,
                        progress.getMeasuredHeight()
                );
                break;

            case MeasureSpec.EXACTLY:
                // do nothing.
                break;

            case MeasureSpec.AT_MOST:
                height = Math.min(
                        height,
                        Math.max(
                                text.getMeasuredHeight() + textMarginVertical * 2,
                                progress.getMeasuredHeight()
                        )
                );
                break;
        }

        ripple.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        );

        setMeasuredDimension(width, height);

        setRadius(height / 2f);

        fingerX = getMeasuredWidth() / 2;
        fingerY = getMeasuredHeight() / 2;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        TransitionManager.beginDelayedTransition(this, new ChangeBounds());
    }

    // touch.

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (state == State.ON || state == State.OFF || listener != null) {
                fingerX = (int) event.getX();
                fingerY = (int) event.getY();
                listener.onSwitch(state);
            }
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
            controlProgress(state);
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

    private void controlProgress(State state) {
        switch (state) {
            case OFF:
            case ON:
                progress.stopAnimation();
                break;

            case TRANSFORM_TO_ON:
            case TRANSFORM_TO_OFF:
                progress.startAnimation();
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
