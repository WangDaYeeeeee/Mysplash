package com.wangdaye.mysplash.ui.widget;

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

import com.wangdaye.mysplash.utils.DisplayUtils;

/**
 * Swipe back layout.
 * */

public class SwipeBackLayout extends CoordinatorLayout {
    // widget
    private View container;
    private View scroller;
    private View statusBar;
    private OnSwipeListener listener;

    // data
    private float swipeDistance = 0;
    private float animDistance = 0;
    private float SWIPE_TRIGGER;
    private static final float SWIPE_RADIO = 2.5F;

    private float touchSlop;
    private float oldY;

    private boolean isLayout = false;
    private int stateNow;
    public static final int NORMAL_STATE = 0;
    public static final int ANIMATING_STATE = 1;

    private int swipeDir = NULL_DIR;
    private static final int NULL_DIR = 0;
    private static final int UP_DIR = 1;
    private static final int DOWN_DIR = -1;

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
            oldY = ev.getY();
        }
        return stateNow != ANIMATING_STATE && isEnabled();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() > oldY && ev.getY() - oldY > touchSlop) {
                    // 下滑
                    return scroller == null || canSwipeBack(DOWN_DIR);
                } else if (ev.getY() < oldY && oldY - ev.getY() > touchSlop) {
                    // 上滑
                    return scroller == null || canSwipeBack(UP_DIR);
                }
                break;

            case MotionEvent.ACTION_UP:
                return swipeDir != NULL_DIR
                        && (scroller == null || canSwipeBack(swipeDir))
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
                    if (swipeDir == 0) {
                        swipeDir = DOWN_DIR;
                        swipeDistance = (float) ((ev.getY() - oldY) / SWIPE_RADIO * 1.0);
                    } else if (swipeDir == UP_DIR) {
                        swipeDistance -= (ev.getY() - oldY) / SWIPE_RADIO * 1.0;
                        if (swipeDistance < 0) {
                            swipeDistance = -swipeDistance;
                            swipeDir = UP_DIR;
                        } else if (swipeDistance == 0) {
                            swipeDir = NULL_DIR;
                        }
                    } else if (swipeDir == DOWN_DIR) {
                        swipeDistance += (ev.getY() - oldY) / SWIPE_RADIO * 1.0;
                    }
                    oldY = ev.getY();

                    setStatusBarAlpha(swipeDistance);
                    requestLayout();
                } else if (ev.getY() < oldY) {
                    // 上滑
                    if (swipeDir == 0) {
                        swipeDir = UP_DIR;
                        swipeDistance = (float) ((oldY - ev.getY()) / SWIPE_RADIO * 1.0);
                    } else if (swipeDir == UP_DIR) {
                        swipeDistance += (oldY - ev.getY()) / SWIPE_RADIO * 1.0;
                    } else if (swipeDir == DOWN_DIR) {
                        swipeDistance -= (oldY - ev.getY()) / SWIPE_RADIO * 1.0;
                        if (swipeDistance < 0) {
                            swipeDistance = -swipeDistance;
                            swipeDir = DOWN_DIR;
                        } else if (swipeDistance == 0) {
                            swipeDir = NULL_DIR;
                        }
                    }
                    oldY = ev.getY();

                    setStatusBarAlpha(swipeDistance);
                    requestLayout();
                }
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    if (swipeDistance >= SWIPE_TRIGGER) {
                        statusBar.setVisibility(View.GONE);
                        swipeBack();
                    } else if (swipeDir != 0) {
                        reset();
                    }
                }
                return true;
        }
        return false;
    }

    // layout.

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isLayout) {
            isLayout = true;
            statusBar = getChildAt(0);
            container = getChildAt(1);
        }
        setMeasuredDimension(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels);
        statusBar.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(DisplayUtils.getStatusBarHeight(getResources()), MeasureSpec.UNSPECIFIED));
        container.measure(
                MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - statusBar.getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        statusBar.layout(0, 0, getMeasuredWidth(), statusBar.getMeasuredHeight());
        switch (swipeDir) {
            case NULL_DIR:
                container.layout(
                        0,
                        statusBar.getMeasuredHeight(),
                        container.getMeasuredWidth(),
                        statusBar.getMeasuredHeight() + container.getMeasuredHeight());
                break;

            case UP_DIR:
                container.layout(
                        0,
                        (int) (statusBar.getMeasuredHeight() - swipeDistance),
                        container.getMeasuredWidth(),
                        (int) (statusBar.getMeasuredHeight() + container.getMeasuredHeight() - swipeDistance));
                break;

            case DOWN_DIR:
                container.layout(
                        0,
                        (int) (statusBar.getMeasuredHeight() + swipeDistance),
                        container.getMeasuredWidth(),
                        (int) (statusBar.getMeasuredHeight() + container.getMeasuredHeight() + swipeDistance));
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
        setState(ANIMATING_STATE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.animDistance = swipeDistance;
            swipeOut.setDuration(300);
            swipeOut.setInterpolator(new DecelerateInterpolator());
            swipeOut.setAnimationListener(new OnSwipeOutListener());
            container.clearAnimation();
            container.startAnimation(swipeOut);
        } else if (listener != null) {
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
        if (swipeDistance >= SWIPE_TRIGGER || swipeDistance == 0) {
            return Color.argb(0, 51, 51, 51);
        }
        return Color.argb((int) (255.0 * (1 - swipeDistance / SWIPE_TRIGGER)), 51, 51, 51);
    }

    /** <br> data */

    public void setState(int stateTo) {
        this.stateNow = stateTo;
    }

    private boolean canSwipeBack(int dir) {
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
        void onSwipeFinish();
    }

    public void setOnSwipeListener(OnSwipeListener listener, View scrollView) {
        this.listener = listener;
        this.scroller = scrollView;
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
