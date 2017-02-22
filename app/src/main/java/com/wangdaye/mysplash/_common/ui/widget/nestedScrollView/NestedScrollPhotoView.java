package com.wangdaye.mysplash._common.ui.widget.nestedScrollView;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.wangdaye.mysplash._common.ui.widget.photoView.PhotoView;

/**
 * Drag photo view.
 * */

public class NestedScrollPhotoView extends PhotoView {
    // widget
    private NestedScrollingParent parent;

    // data
    private boolean isBeingDragged;
    private boolean isNestedScrolling;

    private float oldY;
    private float touchSlop;

    /** <br> life cycle. */

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
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /** <br> touch. */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (parent == null) {
            parent = (NestedScrollingParent) getParent();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getScale() == 1) {
                    isBeingDragged = false;
                    oldY = ev.getY();
                    parent.onStartNestedScroll(this, this, ViewCompat.SCROLL_AXIS_VERTICAL);
                    isNestedScrolling = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (oldY - ev.getY());
                if (getScale() == 1 && !isBeingDragged) {
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
