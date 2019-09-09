package com.wangdaye.common.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.wangdaye.common.ui.widget.photoView.PhotoView;

/**
 * Nested scroll photo view.
 *
 * A PhotoView that can dispatch nested scrolling action.
 *
 * */

public class NestedScrollPhotoView extends PhotoView
        implements NestedScrollingChild3 {

    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private float touchSlop;

    private boolean isBeingDragged;
    private boolean isNestedScrolling;

    private int pointerId;
    private float lastX;
    private float lastY;

    private int[] scrollConsumed;
    private int[] scrollOffsetInWindow;
    private int nestedScrollingOffsetY;

    private static final String TAG = "NestedScrollPhotoView";

    public NestedScrollPhotoView(Context context) {
        super(context);
        this.initialize();
    }

    public NestedScrollPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        this.initialize();
    }

    public NestedScrollPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        this.initialize();
    }

    private void initialize() {
        enable();
        enableRotate();
        setScaleType(ScaleType.CENTER_INSIDE);

        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        nestedScrollingChildHelper.setNestedScrollingEnabled(true);

        touchSlop = 1.5f * ViewConfiguration.get(getContext()).getScaledTouchSlop();

        scrollConsumed = new int[2];
        scrollOffsetInWindow = new int[2];
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean callSuper = true;

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            nestedScrollingOffsetY = 0;
        }
        ev.offsetLocation(0, nestedScrollingOffsetY);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                isNestedScrolling = false;

                pointerId = ev.getPointerId(0);
                lastX = ev.getX();
                lastY = ev.getY();

                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                int index = ev.getActionIndex();
                pointerId = ev.getPointerId(index);
                lastX = ev.getX(index);
                lastY = ev.getY(index);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(pointerId);
                if (index == -1) {
                    Log.e(TAG, "Invalid pointerId=" + pointerId + " in onTouchEvent");
                    break;
                }

                int x = (int) ev.getX(index);
                int y = (int) ev.getY(index);
                if (!isBeingDragged) {
                    if (Math.abs(x - lastX) > touchSlop || Math.abs(y - lastY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(x - lastX) < Math.abs(y - lastY)
                                && getInfo().getScale() <= 1
                                && ev.getPointerCount() == 1) {
                            isNestedScrolling = true;
                            lastY += y > lastY ? touchSlop : -touchSlop;
                        }
                    }
                }

                if (isNestedScrolling) {
                    callSuper = false;

                    int dy = (int) (lastY - y);

                    scrollConsumed[0] = scrollConsumed[1] = 0;
                    dispatchNestedPreScroll(
                            0, dy, scrollConsumed, scrollOffsetInWindow, ViewCompat.TYPE_TOUCH);
                    dy -= scrollConsumed[1];
                    nestedScrollingOffsetY += scrollOffsetInWindow[1];

                    dispatchNestedScroll(
                            0, scrollConsumed[1], 0, dy,
                            scrollOffsetInWindow, ViewCompat.TYPE_TOUCH
                    );
                    nestedScrollingOffsetY += scrollOffsetInWindow[1];

                    lastX = x;
                    lastY = y;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int index = ev.getActionIndex();
                int id = ev.getPointerId(index);
                if (pointerId == id) {
                    int newIndex = index == 0 ? 1 : 0;

                    this.pointerId = ev.getPointerId(newIndex);
                    lastX = (int) ev.getX(newIndex);
                    lastY = (int) ev.getY(newIndex);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                if (isNestedScrolling) {
                    isNestedScrolling = false;
                    callSuper = false;
                }
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
        }

        if (callSuper) {
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }

    // interface.

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
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                        @Nullable int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                                     @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }
}
