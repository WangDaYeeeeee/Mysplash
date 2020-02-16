package com.wangdaye.common.ui.widget.swipeBackView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

/**
 * Swipe back coordinator layout.
 *
 * A {@link CoordinatorLayout} that has swipe back operation.
 *
 * */

public class SwipeBackCoordinatorLayout extends CoordinatorLayout {

    private SwipeBackHelper swipeBackHelper;
    @Nullable private OnSwipeListener swipeListener;

    private int swipeDistance;
    private float swipeTrigger;
    private static final float SWIPE_RADIO = 0.33F;

    private boolean isVerticalDragged;

    @DirectionRule
    private int swipeDir = NULL_DIR;

    public static final int NULL_DIR = 0;
    public static final int UP_DIR = 1;
    public static final int DOWN_DIR = -1;
    @IntDef({NULL_DIR, UP_DIR, DOWN_DIR})
    public @interface DirectionRule {}

    private class ResetAnimation extends Animation {

        private int fromDistance;

        ResetAnimation(int from) {
            this.fromDistance = from;
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            swipeDistance = (int) (fromDistance * (1 - interpolatedTime));
            setSwipeTranslation();
        }
    }

    private Animation.AnimationListener resetAnimListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setEnabled(true);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    };

    public SwipeBackCoordinatorLayout(Context context) {
        super(context);
        this.initialize();
    }

    public SwipeBackCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public SwipeBackCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.swipeBackHelper = new SwipeBackHelper();

        this.swipeDistance = 0;
        this.swipeTrigger = (float) (getResources().getDisplayMetrics().heightPixels / 4.0);
    }

    // nested scroll.

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes, int type) {
        super.onStartNestedScroll(child, target, nestedScrollAxes, type);
        isVerticalDragged = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return type == ViewCompat.TYPE_TOUCH;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        int dyConsumed = 0;
        if (isVerticalDragged && swipeDistance != 0) {
            dyConsumed = onVerticalPreScroll(dy);
        }

        int[] newConsumed = new int[] {0, 0};
        super.onNestedPreScroll(target, dx, dy - dyConsumed, newConsumed, type);

        consumed[0] = newConsumed[0];
        consumed[1] = newConsumed[1] + dyConsumed;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type,
                               @NonNull int[] consumed) {
        int newDyConsumed = dyConsumed;
        int newDyUnconsumed = dyUnconsumed;
        if (isVerticalDragged && swipeDistance == 0) {
            int dir = dyUnconsumed < 0 ? DOWN_DIR : UP_DIR;
            if (swipeListener != null && swipeListener.canSwipeBack(dir)) {
                onVerticalScroll(dyUnconsumed);
                newDyConsumed = dyConsumed + dyUnconsumed;
                newDyUnconsumed = 0;
            }
        }

        super.onNestedScroll(
                target, dxConsumed, newDyConsumed, dxUnconsumed, newDyUnconsumed, type, consumed);
    }

    @Override
    public void onStopNestedScroll(View child, int type) {
        super.onStopNestedScroll(child, type);
        if (isVerticalDragged) {
            if (Math.abs(swipeDistance) >= swipeTrigger) {
                swipeBack();
            } else {
                reset();
            }
        }
    }

    private int onVerticalPreScroll(int dy) {
        int consumed;
        if (swipeDistance * (swipeDistance - dy) < 0) {
            swipeDir = NULL_DIR;
            consumed = swipeDistance;
            swipeDistance = 0;
        } else {
            consumed = dy;
            swipeDistance -= dy;
        }

        setSwipeTranslation();

        return consumed;
    }

    private void onVerticalScroll(int dy) {
        swipeDistance = -dy;
        swipeDir = swipeDistance > 0 ? DOWN_DIR : UP_DIR;

        setSwipeTranslation();
    }

    private void swipeBack() {
        if (swipeListener != null) {
            swipeListener.onSwipeFinish(swipeDir);
        }
    }

    public void reset() {
        swipeDir = NULL_DIR;
        if (swipeDistance != 0) {
            ResetAnimation a = new ResetAnimation(swipeDistance);
            a.setDuration((long) (200.0 + 100.0 * Math.abs(swipeDistance) / swipeTrigger));
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            a.setAnimationListener(resetAnimListener);
            startAnimation(a);
        }
    }

    private void setSwipeTranslation() {
        int dir = swipeDistance > 0 ? UP_DIR : DOWN_DIR;
        drawSwipeFeedback(dir, swipeDistance, swipeTrigger);
        if (swipeListener != null) {
            swipeListener.onSwipeProcess(
                    (float) Math.min(
                            1,
                            Math.abs(1.0 * swipeDistance / swipeTrigger)
                    )
            );
        }
    }

    protected void drawSwipeFeedback(int dir, float swipeDistance, float triggerDistance) {
        setTranslationY(
                (float) (
                        dir * SWIPE_RADIO
                                * triggerDistance
                                * Math.log10(1 + 9.0 * Math.abs(swipeDistance) / triggerDistance)
                )
        );
    }

    /**
     * Whether the SwipeBackView can swipe back.
     *
     * @param v   child view.
     * @param dir drag direction.
     * */
    public static boolean canSwipeBack(View v, int dir) {
        return !v.canScrollVertically(dir);
    }

    /**
     * Compute shadow background alpha by drag percent.
     *
     * @param percent drag percent.
     *
     * @return Color.
     * */
    public static float getBackgroundAlpha(float percent) {
        return (float) (0.9 - percent * (0.9 - 0.5));
    }

    /**
     * Compute shadow background color by drag percent.
     *
     * @param percent drag percent.
     *
     * @return Color.
     * */
    @ColorInt
    public static int getBackgroundColor(float percent) {
        return Color.argb((int) (255 * getBackgroundAlpha(percent)), 0, 0, 0);
    }

    public void prepareViews(SwipeBackActivity activity) {
        swipeBackHelper.prepareViews(activity);
    }

    public void clearViews() {
        swipeBackHelper.clearViews();
    }

    // interface.

    // on swipe listener.

    public interface OnSwipeListener {
        boolean canSwipeBack(@DirectionRule int dir);
        void onSwipeProcess(float percent);
        void onSwipeFinish(@DirectionRule int dir);
    }

    public void setOnSwipeListener(@Nullable OnSwipeListener l) {
        this.swipeListener = l;
    }
}