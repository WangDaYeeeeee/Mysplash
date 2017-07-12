package com.wangdaye.mysplash.common.ui.widget.nestedScrollView;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomTouchView;

/**
 * Nested scroll freedom touch view.
 *
 * A freedom touch view that implements nested scrolling child interface.
 *
 * */

public class NestedScrollFreedomTouchView extends FreedomTouchView
        implements NestedScrollingChild {

    private NestedScrollingChildHelper nestedScrollingChildHelper;

    private boolean isBeingDragged;
    private boolean isHorizontalDragged;

    private float initX;
    private float initY;

    private float oldX;

    private float touchSlop;

    public NestedScrollFreedomTouchView(Context context) {
        super(context);
        this.initialize();
    }

    public NestedScrollFreedomTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public NestedScrollFreedomTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                getContext().getResources().getDisplayMetrics().heightPixels);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                isHorizontalDragged = false;
                initX = ev.getX();
                initY = ev.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isBeingDragged) {
                    return isHorizontalDragged;
                } else {
                    if (Math.abs(ev.getX() - initX) > touchSlop
                            || Math.abs(ev.getY() - initY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(ev.getX() - initX) > Math.abs(ev.getY() - initY)) {
                            isHorizontalDragged = true;
                            oldX = ev.getX();
                            return true;
                        } else {
                            isHorizontalDragged = false;
                            return false;
                        }
                    } else {
                        initX = ev.getX();
                        initY = ev.getY();
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                isHorizontalDragged = false;
                stopNestedScroll();
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getX() - oldX;
                int[] consumed = new int[2];
                if (dispatchNestedPreScroll((int) deltaX, 0, consumed, null)) {
                    dispatchNestedScroll(
                            consumed[0], consumed[1],
                            (int) (deltaX - consumed[0]), -consumed[1],
                            null);
                }
                break;
        }
        return false;
    }

    // nested scrolling child.

    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
