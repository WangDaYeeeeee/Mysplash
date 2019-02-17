package com.wangdaye.mysplash.common.ui.widget.nestedScrollView;

import android.animation.ValueAnimator;
import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

/**
 * Nested scroll app bar layout.
 *
 * An AppBarLayout that can dispatch nested scrolling action.
 *
 * */

public class NestedScrollAppBarLayout extends AppBarLayout
        implements CoordinatorLayout.AttachedBehavior, NestedScrollingChild3 {

    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private OnNestedScrollingListener nestedScrollingListener;

    private AutomaticScrollAnimator animator;

    private float touchSlop;

    private float startY;

    // an appbar has 3 part : scroll / enterAlways / without scroll flag.
    private int scrollHeight;
    private int enterAlwaysHeight;
    private int staticHeight;

    public static class Behavior extends AppBarLayout.Behavior {

        private NestedScrollAppBarLayout appBarLayout = null;

        private float oldY;
        private boolean isBeingDragged;

        public Behavior() {
            super();
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private void bindAppBar(AppBarLayout child) {
            if (appBarLayout == null) {
                this.appBarLayout = (NestedScrollAppBarLayout) child;
            }
        }

        @Override
        public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            bindAppBar(child);
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    appBarLayout.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                    oldY = ev.getY();
                    isBeingDragged = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!isBeingDragged) {
                        if (Math.abs(ev.getY() - oldY) > appBarLayout.getTouchSlop()) {
                            isBeingDragged = true;
                        }
                    }
                    if (isBeingDragged) {
                        int[] total = new int[] {0, (int) (oldY - ev.getY())};
                        int[] consumed = new int[] {0, 0};
                        appBarLayout.dispatchNestedPreScroll(
                                total[0], total[1], consumed, null);
                        appBarLayout.dispatchNestedScroll(
                                consumed[0], consumed[1],
                                total[0] - consumed[0], total[1] - consumed[1],
                                null);
                    }
                    oldY = ev.getY();
                    return isBeingDragged;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    appBarLayout.stopNestedScroll();
                    if (isBeingDragged) {
                        isBeingDragged = false;
                        return true;
                    }
                    break;
            }

            return super.onTouchEvent(parent, child, ev);
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
                                           View directTargetChild, View target, int nestedScrollAxes, int type) {
            if (super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
                    && type == ViewCompat.TYPE_TOUCH) {
                bindAppBar(child);
                if (appBarLayout.nestedScrollingListener != null) {
                    appBarLayout.nestedScrollingListener.onStartNestedScroll();
                }
                appBarLayout.stopScrollAnimator();
                appBarLayout.setStartY(child.getY());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                      View target, int dx, int dy, int[] consumed, int type) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
            bindAppBar(child);
            if (appBarLayout.nestedScrollingListener != null) {
                appBarLayout.nestedScrollingListener.onNestedScrolling();
            }
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child,
                                   @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                                   int dyUnconsumed, @ViewCompat.NestedScrollType int type, @NonNull int[] consumed) {
            super.onNestedScroll(
                    coordinatorLayout, child, target,
                    dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
            bindAppBar(child);
            if (appBarLayout.nestedScrollingListener != null) {
                appBarLayout.nestedScrollingListener.onNestedScrolling();
            }
        }

        @Override
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                       View target, int type) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type);
            bindAppBar(child);
            if (appBarLayout.nestedScrollingListener != null) {
                appBarLayout.nestedScrollingListener.onStopNestedScroll();
            }

            float top = child.getY();
            float height = child.getMeasuredHeight();
            float bottom = top + height;
            appBarLayout.computerHeightData();

            if (appBarLayout.scrollHeight > 0 || appBarLayout.enterAlwaysHeight > 0) {
                if (appBarLayout.getStartY() == top) {
                    return;
                } if (appBarLayout.getStartY() > top) {  // drag up.
                    appBarLayout.hideTopBar(this);
                } else if (appBarLayout.getStartY() < top) { // drag down.
                    if (bottom > appBarLayout.enterAlwaysHeight + appBarLayout.staticHeight) {
                        appBarLayout.showTopBar(this);
                    } else if (bottom > appBarLayout.staticHeight) {
                        appBarLayout.showEnterAlwaysBar(this);
                    }
                }
            }
        }
    }

    private class AutomaticScrollAnimator extends ValueAnimator {

        private int lastY;

        AutomaticScrollAnimator(final AppBarLayout.Behavior behavior, final int toY) {
            final int fromY = (int) getY();
            this.lastY = fromY;

            setIntValues(fromY, toY);
            setDuration((long) (150.0 + 150.0 * Math.abs(toY - fromY) / getMeasuredHeight()));
            setInterpolator(new DecelerateInterpolator());
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (behavior != null) {
                        int newY = (int) animation.getAnimatedValue();
                        int[] total = new int[] {0, lastY - newY};
                        int[] consumed = new int[] {0, 0};
                        behavior.onNestedPreScroll(
                                (CoordinatorLayout) getParent(), NestedScrollAppBarLayout.this,
                                NestedScrollAppBarLayout.this, total[0], total[1],
                                consumed, ViewCompat.TYPE_TOUCH);
                        behavior.onNestedScroll(
                                (CoordinatorLayout) getParent(), NestedScrollAppBarLayout.this,
                                NestedScrollAppBarLayout.this, consumed[0], consumed[1],
                                total[0] - consumed[0], total[1] - consumed[1],
                                ViewCompat.TYPE_TOUCH, consumed);
                        lastY = newY;
                    }
                }
            });
        }
    }

    public NestedScrollAppBarLayout(Context context) {
        super(context);
        this.initialize();
    }

    public NestedScrollAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    private void initialize() {
        this.nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        this.nestedScrollingChildHelper.setNestedScrollingEnabled(true);
        setNestedScrollingEnabled(true);

        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * Do animation to expand the whole AppBarLayout.
     * */
    public void showTopBar(AppBarLayout.Behavior behavior) {
        stopScrollAnimator();
        doScrollAnimation(behavior, 0);
    }

    /**
     * Do animation to expand the part of AppBarLayout which has "enterAlways" flag.
     * */
    public void showEnterAlwaysBar(AppBarLayout.Behavior behavior) {
        stopScrollAnimator();
        doScrollAnimation(behavior, -scrollHeight);
    }

    /**
     * Do animation to hide the part of AppBarLayout which has "scroll" flag.
     * */
    public void hideTopBar(AppBarLayout.Behavior behavior) {
        stopScrollAnimator();
        doScrollAnimation(behavior, staticHeight - getMeasuredHeight());
    }

    private void doScrollAnimation(AppBarLayout.Behavior behavior, int toY) {
        if (getY() != toY) {
            this.animator = new AutomaticScrollAnimator(behavior, toY);
            animator.start();
        }
    }

    public void stopScrollAnimator() {
        if (animator != null) {
            animator.cancel();
        }
    }

    public float getTouchSlop() {
        return touchSlop;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    /**
     * compute the height of three part in AppBarLayout.
     * */
    void computerHeightData() {
        scrollHeight = enterAlwaysHeight = staticHeight = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            View v = getChildAt(i);
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            int flags = params.getScrollFlags();
            if ((flags & LayoutParams.SCROLL_FLAG_SNAP) == LayoutParams.SCROLL_FLAG_SNAP) {
                scrollHeight = enterAlwaysHeight = staticHeight = 0;
                return;
            } else if ((flags & LayoutParams.SCROLL_FLAG_SCROLL) != LayoutParams.SCROLL_FLAG_SCROLL) {
                staticHeight += v.getMeasuredHeight();
            } else if ((flags & LayoutParams.SCROLL_FLAG_ENTER_ALWAYS) == LayoutParams.SCROLL_FLAG_ENTER_ALWAYS) {
                enterAlwaysHeight += v.getMeasuredHeight();
            } else {
                scrollHeight += v.getMeasuredHeight();
            }
        }
    }

    // interface.

    // on nested scrolling listener.

    public interface OnNestedScrollingListener {
        void onStartNestedScroll();
        void onNestedScrolling();
        void onStopNestedScroll();
    }

    public void setOnNestedScrollingListener(OnNestedScrollingListener l) {
        this.nestedScrollingListener = l;
    }

    // attached behavior.

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new Behavior();
    }

    // nested scrolling child.

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return nestedScrollingChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        nestedScrollingChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return nestedScrollingChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy,
                                           @Nullable int[] consumed, @Nullable int[] offsetInWindow,
                                           int type) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                     int dxUnconsumed, int dyUnconsumed,
                                     @Nullable int[] offsetInWindow, int type,
                                     @NonNull int[] consumed) {
        nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed,
                                        @Nullable int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }
}
