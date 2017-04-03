package com.wangdaye.mysplash.common.ui.widget.nestedScrollView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * Nested scroll app bar layout.
 *
 * An AppBarLayout that can dispatch nested scrolling action.
 *
 * */

@CoordinatorLayout.DefaultBehavior(NestedScrollAppBarLayout.Behavior.class)
public class NestedScrollAppBarLayout extends AppBarLayout
        implements NestedScrollingChild {
    // widget
    private NestedScrollingChildHelper nestedScrollingChildHelper;
    OnNestedScrollingListener nestedScrollingListener;

    private AutomaticScrollAnimator animator;

    // data
    private float touchSlop;

    private float startY;

    // an appbar has 3 part : scroll / enterAlways / without scroll flag.
    private int scrollHeight;
    private int enterAlwaysHeight;
    private int staticHeight;

    /** <br> life cycle. */

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
        setNestedScrollingEnabled(true);

        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /** <br> UI. */

    /**
     * Do animation to expand the whole AppBarLayout.
     * */
    public void showTopBar() {
        stopScrollAnimator();
        doScrollAnimation(0);
    }

    /**
     * Do animation to expand the part of AppBarLayout which has "enterAlways" flag.
     * */
    public void showEnterAlwaysBar() {
        stopScrollAnimator();
        doScrollAnimation(-scrollHeight);
    }

    /**
     * Do animation to hide the part of AppBarLayout which has "scroll" flag.
     * */
    public void hideTopBar() {
        stopScrollAnimator();
        doScrollAnimation(staticHeight - getMeasuredHeight());
    }

    private void doScrollAnimation(int toY) {
        if (getY() != toY) {
            this.animator = new AutomaticScrollAnimator(toY);
            animator.start();
        }
    }

    public void stopScrollAnimator() {
        if (animator != null) {
            animator.cancel();
        }
    }

    /** <br> data. */

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

    /** <br> interface. */

    // on nested scrolling listener.

    public interface OnNestedScrollingListener {
        void onStartNestedScroll();
        void onNestedScrolling();
        void onStopNestedScroll();
    }

    public void setOnNestedScrollingListener(OnNestedScrollingListener l) {
        this.nestedScrollingListener = l;
    }

    // nested scrolling child.

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    /** <br> inner class. */

    // behavior.

    public static class Behavior extends AppBarLayout.Behavior {
        // widget
        private NestedScrollAppBarLayout appBarLayout = null;

        // data
        private float oldY;
        private boolean isBeingDragged;

        // life cycle.

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

        // touch.

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
                                consumed[0], consumed[1], total[0] - consumed[0], total[1] - consumed[1], null);
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

        // nested scroll.

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
                                           View directTargetChild, View target, int nestedScrollAxes) {
            if (super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes)) {
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
                                      View target, int dx, int dy, int[] consumed) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
            bindAppBar(child);
            if (appBarLayout.nestedScrollingListener != null) {
                appBarLayout.nestedScrollingListener.onNestedScrolling();
            }
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                   View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed) {
            super.onNestedScroll(
                    coordinatorLayout, child, target,
                    dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            bindAppBar(child);
            if (appBarLayout.nestedScrollingListener != null) {
                appBarLayout.nestedScrollingListener.onNestedScrolling();
            }
        }

        @Override
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                       View target) {
            super.onStopNestedScroll(coordinatorLayout, child, target);
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
                    appBarLayout.hideTopBar();
                } else if (appBarLayout.getStartY() < top) { // drag down.
                    if (bottom > appBarLayout.enterAlwaysHeight + appBarLayout.staticHeight) {
                        appBarLayout.showTopBar();
                    } else if (bottom > appBarLayout.staticHeight) {
                        appBarLayout.showEnterAlwaysBar();
                    }
                }
            }
        }
    }

    // animation.

    private class AutomaticScrollAnimator extends ValueAnimator {
        // widget
        private CoordinatorLayout.Behavior behavior = null;

        // data
        private int lastY;

        // life cycle.

        AutomaticScrollAnimator(final int toY) {
            final ViewGroup.LayoutParams params = getLayoutParams();
            if (params instanceof CoordinatorLayout.LayoutParams) {
                behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            }

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
                                (CoordinatorLayout) getParent(), NestedScrollAppBarLayout.this, null,
                                total[0], total[1], consumed);
                        behavior.onNestedScroll(
                                (CoordinatorLayout) getParent(), NestedScrollAppBarLayout.this, null,
                                consumed[0], consumed[1], total[0] - consumed[0], total[1] - consumed[1]);
                        lastY = newY;
                    }
                }
            });
        }
    }
}
