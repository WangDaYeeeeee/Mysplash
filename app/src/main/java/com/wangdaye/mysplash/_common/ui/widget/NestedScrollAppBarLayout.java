package com.wangdaye.mysplash._common.ui.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Nested scroll app bar layout.
 * */

@CoordinatorLayout.DefaultBehavior(NestedScrollAppBarLayout.Behavior.class)
public class NestedScrollAppBarLayout extends AppBarLayout
        implements NestedScrollingChild, NestedScrollingParent, GestureDetector.OnGestureListener {
    // widget
    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private GestureDetector detector;

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
        this.detector = new GestureDetector(getContext(), this);

        setNestedScrollingEnabled(true);
    }

    /** <br> interface. */

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

    // on gesture listener.

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        // do nothing.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        int[] total = new int[] {(int) v, (int) v1};
        int[] consumed = new int[] {0, 0};
        dispatchNestedPreScroll(total[0], total[1], consumed, null);
        dispatchNestedScroll(0, 0, total[0] - consumed[0], total[1] - consumed[1], null);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        // do nothing.
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        dispatchNestedPreFling(v, v1);
        dispatchNestedFling(v, v1, false);
        return false;
    }

    /** <br> inner class. */

    public static class Behavior extends AppBarLayout.Behavior {

        public Behavior() {
            super();
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            return super.onInterceptTouchEvent(parent, child, ev);
        }

        @Override
        public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            NestedScrollAppBarLayout appBarLayout = (NestedScrollAppBarLayout) child;
            final boolean handled = appBarLayout.detector.onTouchEvent(ev);
            if (!handled && ev.getAction() == MotionEvent.ACTION_UP) {
                appBarLayout.stopNestedScroll();
            }

            return super.onTouchEvent(parent, child, ev);
        }
    }
}
