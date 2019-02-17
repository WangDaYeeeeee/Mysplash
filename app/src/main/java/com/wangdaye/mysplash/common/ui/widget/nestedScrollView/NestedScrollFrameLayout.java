package com.wangdaye.mysplash.common.ui.widget.nestedScrollView;

import android.content.Context;
import android.os.Build;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;

/**
 * Nested scroll frame layout.
 *
 * This FrameLayout can dispatch nested scrolling action.
 *
 * */

public abstract class NestedScrollFrameLayout extends FrameLayout
        implements NestedScrollingChild3, NestedScrollingParent3 {

    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private NestedScrollingParentHelper nestedScrollingParentHelper;

    private boolean forceScrolling;

    private boolean isBeingDragged;
    @DirectionRule
    private int swipeDir;
    private float oldY;
    private int lastOffsetY;
    private float touchSlop;

    private static final int DIR_TOP = 1;
    private static final int DIR_BOTTOM = -1;
    private static final int DIR_NULL = 0;
    @IntDef({DIR_TOP, DIR_BOTTOM, DIR_NULL})
    private @interface DirectionRule {}

    public NestedScrollFrameLayout(Context context) {
        super(context);
        this.initialize();
    }

    public NestedScrollFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public NestedScrollFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NestedScrollFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        this.nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        this.nestedScrollingChildHelper.setNestedScrollingEnabled(true);
        this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        setNestedScrollingEnabled(true);

        setForceScrolling(false);

        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (forceScrolling) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isBeingDragged = false;
                    swipeDir = DIR_NULL;
                    oldY = ev.getY();
                    lastOffsetY = 0;
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                    return false;

                case MotionEvent.ACTION_MOVE:
                    int deltaY = (int) (oldY - ev.getY() + lastOffsetY);
                    if (!isBeingDragged) {
                        if (Math.abs(deltaY) > touchSlop) {
                            isBeingDragged = true;
                        } else {
                            swipeDir = DIR_NULL;
                            oldY = ev.getY();
                            lastOffsetY = 0;
                        }
                    }
                    return isBeingDragged;
            }
        } else {
            super.onInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (forceScrolling) {
                    return false;
                } else {
                    isBeingDragged = false;
                    swipeDir = DIR_NULL;
                    oldY = ev.getY();
                    lastOffsetY = 0;
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                    break;
                }

            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (oldY - ev.getY() + lastOffsetY);
                if (!isBeingDragged && !forceScrolling) {
                    if (Math.abs(deltaY) > touchSlop) {
                        isBeingDragged = true;
                    } else {
                        swipeDir = DIR_NULL;
                        oldY = ev.getY();
                        lastOffsetY = 0;
                    }
                }
                if (isBeingDragged) {
                    if (swipeDir == DIR_NULL
                            || swipeDir * deltaY > 0 || (swipeDir * deltaY < 0 && Math.abs(deltaY) > touchSlop)) {
                        int[] total = new int[] {0, deltaY};
                        int[] consumed = new int[] {0, 0};
                        int y = (int) (isParentOffset () ? ((View) getParent()).getY() : getY());
                        if (dispatchNestedPreScroll(total[0], total[1], consumed, null, ViewCompat.TYPE_TOUCH)) {
                            total[0] -= consumed[0];
                            total[1] -= consumed[1];
                        }
                        dispatchNestedScroll(consumed[0], consumed[1], total[0], total[1], null, ViewCompat.TYPE_TOUCH);
                        swipeDir = deltaY == 0 ? DIR_NULL : (deltaY > 0 ? DIR_TOP : DIR_BOTTOM);
                        oldY = ev.getY();
                        lastOffsetY = (int) (y - (isParentOffset () ? ((View) getParent()).getY() : getY()));
                    }
                }
                if (forceScrolling) {
                    return isBeingDragged;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
        }
        return true;
    }

    /**
     * If return true, the view will eliminate the error by compute the offset of it's parent view.
     * */
    public abstract boolean isParentOffset();

    public void setForceScrolling(boolean forceScrolling) {
        this.forceScrolling = forceScrolling;
    }

    // interface.

    // nested scrolling parent.

    @Override
    public boolean onStartNestedScroll(@NotNull View child, @NotNull View target,
                                       int nestedScrollAxes, int type) {
        if ((nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                && type == ViewCompat.TYPE_TOUCH) {
            startNestedScroll(nestedScrollAxes, type);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes, int type) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        stopNestedScroll(type);
        nestedScrollingParentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NotNull int[] consumed, int type) {
        dispatchNestedPreScroll(dx, dy, consumed, null, type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed, int type, @NonNull int[] consumed) {
        dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type, consumed);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed, int type) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null, type);
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
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                     @Nullable int[] offsetInWindow, int type,
                                     @NonNull int[] consumed) {
        nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        @Nullable int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }
}
