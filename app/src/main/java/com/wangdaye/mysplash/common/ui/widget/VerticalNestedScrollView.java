package com.wangdaye.mysplash.common.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Vertical nested scroll view.
 * */

public class VerticalNestedScrollView extends NestedScrollView {

    private int pointerId;
    private float initialX;
    private float initialY;
    private int touchSlop;

    private boolean isBeingDragged = false;
    private boolean isHorizontalDragged = false;

    private static final String TAG = "VerticalNestedScrollV";

    public VerticalNestedScrollView(Context context) {
        super(context);
        this.initialize();
    }

    public VerticalNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public VerticalNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                isHorizontalDragged = false;

                pointerId = ev.getPointerId(0);
                initialX = ev.getX();
                initialY = ev.getY();
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                int index = ev.getActionIndex();
                pointerId = ev.getPointerId(index);
                initialX = ev.getX(index);
                initialY = ev.getY(index);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(pointerId);
                if (index == -1) {
                    Log.e(TAG, "Invalid pointerId=" + pointerId + " in onTouchEvent");
                    break;
                }

                float x = ev.getX(index);
                float y = ev.getY(index);

                if (!isBeingDragged && !isHorizontalDragged) {
                    if (Math.abs(x - initialX) > touchSlop || Math.abs(y - initialY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(x - initialX) > Math.abs(y - initialY)) {
                            isHorizontalDragged = true;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int index = ev.getActionIndex();
                int id = ev.getPointerId(index);
                if (pointerId == id) {
                    int newIndex = index == 0 ? 1 : 0;

                    this.pointerId = ev.getPointerId(newIndex);
                    initialX = (int) ev.getX(newIndex);
                    initialY = (int) ev.getY(newIndex);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                isHorizontalDragged = false;
                break;
        }

        return result && isBeingDragged && !isHorizontalDragged;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev) && isBeingDragged && !isHorizontalDragged;
    }
}