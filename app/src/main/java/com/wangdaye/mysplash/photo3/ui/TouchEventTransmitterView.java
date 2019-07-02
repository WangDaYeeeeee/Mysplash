package com.wangdaye.mysplash.photo3.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class TouchEventTransmitterView extends View {

    @Nullable private View target;

    public TouchEventTransmitterView(Context context) {
        this(context, null);
    }

    public TouchEventTransmitterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchEventTransmitterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTarget(@Nullable View v) {
        target = v;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (target != null) {
            if (target.getMeasuredWidth() != 0 && target.getMeasuredHeight() != 0) {
                setMeasuredDimension(target.getMeasuredWidth(), target.getMeasuredHeight());
            } else {
                target.measure(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(target.getMeasuredWidth(), target.getMeasuredHeight());
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (target != null && target.dispatchTouchEvent(event)) {
            return true;
        }

        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (target != null && target.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }
}
