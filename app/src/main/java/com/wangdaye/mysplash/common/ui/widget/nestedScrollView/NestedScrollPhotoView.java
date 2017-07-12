package com.wangdaye.mysplash.common.ui.widget.nestedScrollView;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.photoView.PhotoView;

/**
 * Nested scroll photo view.
 *
 * A PhotoView that can dispatch nested scrolling action.
 *
 * */

public class NestedScrollPhotoView extends PhotoView {

    private NestedScrollingParent parent;

    private boolean isBeingDragged;
    private boolean isNestedScrolling;

    private float oldY;
    private float touchSlop;

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

        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent == null) {
            parent = (NestedScrollingParent) getParent();
        }

        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                if (getInfo().getScale() == 1) {
                    isBeingDragged = false;
                    oldY = ev.getY();
                    parent.onStartNestedScroll(this, this, ViewCompat.SCROLL_AXIS_VERTICAL);
                    isNestedScrolling = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (oldY - ev.getY());
                if (getInfo().getScale() == 1 && !isBeingDragged) {
                    if (Math.abs(deltaY) > touchSlop) {
                        isBeingDragged = true;
                    }
                }
                if (isBeingDragged) {
                    int[] total = new int[] {0, deltaY};
                    int[] consumed = new int[] {0, 0};
                    parent.onNestedPreScroll(this, total[0], total[1], consumed);
                    total[0] -= consumed[0];
                    total[1] -= consumed[1];
                    parent.onNestedScroll(this, consumed[0], consumed[1], total[0], total[1]);
                }
                oldY = ev.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isBeingDragged = false;
                if (isNestedScrolling) {
                    isNestedScrolling = false;
                    if (parent instanceof SwipeBackCoordinatorLayout) {
                        ((SwipeBackCoordinatorLayout) parent).reset();
                    } else {
                        parent.onStopNestedScroll(this);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                if (isNestedScrolling) {
                    isNestedScrolling = false;
                    parent.onStopNestedScroll(this);
                }
                break;
        }

        super.dispatchTouchEvent(ev);
        return true;
    }
}
