package com.wangdaye.mysplash.common.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Swipe back coordinator layout.
 *
 * A {@link CoordinatorLayout} that has swipe back operation.
 *
 * */

public class SwipeBackCoordinatorLayout extends CoordinatorLayout {

    private OnSwipeListener swipeListener;

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

    private static class RecolorAnimation extends Animation {

        private View view;
        private boolean showing;

        RecolorAnimation(View v, boolean showing) {
            this.view = v;
            this.showing = showing;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (showing) {
                view.setBackgroundColor(Color.argb((int) (255 * 0.5 * interpolatedTime), 0, 0, 0));
            } else {
                view.setBackgroundColor(Color.argb((int) (255 * 0.5 * (1 - interpolatedTime)), 0, 0, 0));
            }
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
        this.swipeDistance = 0;
        this.swipeTrigger = (float) (getResources().getDisplayMetrics().heightPixels / 4.0);
    }

    // nested scroll.

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        super.onStartNestedScroll(child, target, nestedScrollAxes);
        isVerticalDragged = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        int dyConsumed = 0;
        if (isVerticalDragged && swipeDistance != 0) {
            dyConsumed = onVerticalPreScroll(dy);
        }

        int[] newConsumed = new int[] {0, 0};
        super.onNestedPreScroll(target, dx, dy - dyConsumed, newConsumed);

        consumed[0] = newConsumed[0];
        consumed[1] = newConsumed[1] + dyConsumed;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
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

        super.onNestedScroll(target, dxConsumed, newDyConsumed, dxUnconsumed, newDyUnconsumed);
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
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
        setTranslationY(
                (float) (dir * SWIPE_RADIO * swipeTrigger
                        * Math.log10(1 + 9.0 * Math.abs(swipeDistance) / swipeTrigger)));
        if (swipeListener != null) {
            swipeListener.onSwipeProcess(
                    (float) Math.min(
                            1,
                            Math.abs(1.0 * swipeDistance / swipeTrigger)));
        }
    }

    /**
     * Whether the SwipeBackView can swipe back.
     *
     * @param v   child view.
     * @param dir drag direction.
     * */
    public static boolean canSwipeBack(View v, int dir) {
        return !ViewCompat.canScrollVertically(v, dir);
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
        return Color.argb((int) (255 * (0.9 - percent * (0.9 - 0.5))), 0, 0, 0);
    }

    /**
     * Execute fade animation to hide shadow background.
     *
     * @param background The view to show shadow background.
     * */
    public static void hideBackgroundShadow(View background) {
        RecolorAnimation a = new RecolorAnimation(background, false);
        a.setDuration(200);
        background.startAnimation(a);
    }

    // interface.

    // on swipe listener.

    public interface OnSwipeListener {
        boolean canSwipeBack(int dir);
        void onSwipeProcess(float percent);
        void onSwipeFinish(int dir);
    }

    public void setOnSwipeListener(OnSwipeListener l) {
        this.swipeListener = l;
    }
}