package com.wangdaye.mysplash._common.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Swipe back layout.
 * */

public class SwipeBackLayout extends CoordinatorLayout {
    // widget
    private View container;
    private View statusBar;
    private OnSwipeListener listener;

    // data
    private float swipeDistance = 0;
    private float animDistance = 0;
    private float SWIPE_TRIGGER;
    private static final float SWIPE_RADIO = 2.5F;

    private float touchSlop;
    private float oldX;
    private float oldY;
    private boolean swiping = false;

    private boolean hasLayout = false;
    private int stateNow;
    public static final int NORMAL_STATE = 0;
    public static final int ANIMATING_STATE = 1;
    public static final int BACKING_STATE = 2;

    private int swipeDir = NULL_DIR;
    public static final int NULL_DIR = 0;
    public static final int UP_DIR = 1;
    public static final int DOWN_DIR = -1;

    /** <br> life cycle. */

    public SwipeBackLayout(Context context) {
        super(context);
        this.initialize();
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.SWIPE_TRIGGER = (float) (getResources().getDisplayMetrics().heightPixels / 10.0);
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        setState(NORMAL_STATE);
        setWillNotDraw(false);
    }

    /** <br> parent methods. */

    // touch.

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            oldX = ev.getX();
            oldY = ev.getY();
        }
        return stateNow == NORMAL_STATE && isEnabled();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() > oldY && ev.getY() - oldY > touchSlop
                        && Math.abs(ev.getX() - oldX) < Math.abs(ev.getY() - oldY)) {
                    // 下滑
                    if (listener != null && !listener.canSwipeBack(DOWN_DIR) && !swiping) {
                        oldX = ev.getX();
                        oldY = ev.getY();
                    }
                    return listener != null && (listener.canSwipeBack(DOWN_DIR) || swiping);
                } else if (ev.getY() < oldY && oldY - ev.getY() > touchSlop
                        && Math.abs(ev.getX() - oldX) < Math.abs(ev.getY() - oldY)) {
                    // 上滑
                    if (listener != null && !listener.canSwipeBack(UP_DIR) && !swiping) {
                        oldX = ev.getX();
                        oldY = ev.getY();
                    }
                    return listener != null && (listener.canSwipeBack(UP_DIR) || swiping);
                }
                break;

            case MotionEvent.ACTION_UP:
                return swipeDir != NULL_DIR
                        && (listener != null && (listener.canSwipeBack(swipeDir) || swiping))
                        && Math.abs(ev.getY() - oldY) > touchSlop;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (ev.getY() > oldY) {
                    // 下滑
                    swipeDistance = (ev.getY() - oldY) / SWIPE_RADIO;
                    swipeDir = DOWN_DIR;
                    setStatusBarAlpha(swipeDistance);
                    requestLayout();
                } else if (ev.getY() < oldY) {
                    // 上滑
                    swipeDistance = (oldY - ev.getY()) / SWIPE_RADIO;
                    swipeDir = UP_DIR;
                    setStatusBarAlpha(swipeDistance);
                    requestLayout();
                } else {
                    swipeDistance = 0;
                    swipeDir = NULL_DIR;
                    setStatusBarAlpha(swipeDistance);
                    requestLayout();
                }
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    swiping = false;
                    if (swipeDistance >= SWIPE_TRIGGER) {
                        if (statusBar != null) {
                            statusBar.setVisibility(View.GONE);
                        }
                        swipeBack();
                    } else if (swipeDir != NULL_DIR) {
                        reset();
                    }
                } else {
                    swiping = true;
                }
                return true;
        }
        return false;
    }

    // layout.

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!hasLayout) {
            hasLayout = true;
            if (getChildCount() > 1) {
                statusBar = getChildAt(0);
                container = getChildAt(1);
            } else {
                statusBar = null;
                container = getChildAt(0);
            }
        }
        setMeasuredDimension(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels);
        int statusBarHeight = statusBar == null ? 0 : DisplayUtils.getStatusBarHeight(getResources());
        if (statusBar != null) {
            statusBar.measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(DisplayUtils.getStatusBarHeight(getResources()), MeasureSpec.UNSPECIFIED));
        }
        container.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - statusBarHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int statusBarHeight = statusBar == null ? 0 : DisplayUtils.getStatusBarHeight(getResources());
        if (statusBar != null) {
            statusBar.layout(0, 0, getMeasuredWidth(), statusBarHeight);
        }
        switch (swipeDir) {
            case NULL_DIR:
                container.layout(
                        0,
                        statusBarHeight,
                        container.getMeasuredWidth(),
                        statusBarHeight + container.getMeasuredHeight());
                break;

            case UP_DIR:
                container.layout(
                        0,
                        (int) (statusBarHeight - swipeDistance),
                        container.getMeasuredWidth(),
                        (int) (statusBarHeight + container.getMeasuredHeight() - swipeDistance));
                break;

            case DOWN_DIR:
                container.layout(
                        0,
                        (int) (statusBarHeight + swipeDistance),
                        container.getMeasuredWidth(),
                        (int) (statusBarHeight + container.getMeasuredHeight() + swipeDistance));
                break;
        }
    }

    // draw.

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(getShadowColor());
    }

    /** <br> UI. */

    private void reset() {
        setState(ANIMATING_STATE);
        this.animDistance = swipeDistance;

        reset.setDuration(300);
        reset.setInterpolator(new AccelerateDecelerateInterpolator());
        reset.setAnimationListener(new OnResetListener());
        container.clearAnimation();
        container.startAnimation(reset);
    }

    private void swipeBack() {
        setState(BACKING_STATE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.animDistance = swipeDistance;
            swipeOut.setDuration(300);
            swipeOut.setInterpolator(new DecelerateInterpolator());
            swipeOut.setAnimationListener(new OnSwipeOutListener());
            container.clearAnimation();
            container.startAnimation(swipeOut);
        } else if (listener != null) {
            invalidate();
            listener.onSwipeFinish();
        }
    }

    private void setStatusBarAlpha(float swipeLength) {
        if (statusBar == null) {
            return;
        }
        if (swipeLength < SWIPE_TRIGGER) {
            statusBar.setAlpha((float) (1 - swipeLength * 1.0 / SWIPE_TRIGGER));
            invalidate();
        } else {
            statusBar.setAlpha(0);
            invalidate();
        }
    }

    private int getShadowColor() {
        if (swipeDistance == 0 || swipeDistance > SWIPE_TRIGGER) {
            return Color.argb(0, 0, 0, 0);
        }
        return Color.argb((int) (255.0 * (1 - swipeDistance / SWIPE_TRIGGER)), 0, 0, 0);
    }

    /** <br> data */

    public void setState(int stateTo) {
        this.stateNow = stateTo;
    }

    public static boolean canSwipeBack(View scroller, int dir) {
        return scroller != null && !ViewCompat.canScrollVertically(scroller, dir);
    }

    /** <br> animations. */

    private Animation reset = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            // distance = -anim * x + anim
            swipeDistance = animDistance * (1 - interpolatedTime);
            requestLayout();
            setStatusBarAlpha(swipeDistance);
        }
    };

    private Animation swipeOut = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            // distance = (9 * anim) * x + anim
            swipeDistance = (float) (animDistance * (1.0 + 9.0 * interpolatedTime));
            requestLayout();
            setStatusBarAlpha(swipeDistance);
        }
    };

    /** <br> interface. */

    public interface OnSwipeListener {
        boolean canSwipeBack(int dir);
        void onSwipeFinish();
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    // animation listener.

    private class OnResetListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // do nothing.
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            swipeDistance = 0;
            animDistance = 0;
            swipeDir = NULL_DIR;
            requestLayout();
            setStatusBarAlpha(swipeDistance);
            setState(NORMAL_STATE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    }

    private class OnSwipeOutListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (listener != null) {
                listener.onSwipeFinish();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
