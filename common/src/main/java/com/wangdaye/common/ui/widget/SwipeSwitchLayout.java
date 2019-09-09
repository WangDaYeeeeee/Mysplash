package com.wangdaye.common.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.ViewCompat;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;

/**
 * Swipe switch layout.
 * */

public class SwipeSwitchLayout extends FrameLayout
        implements NestedScrollingParent3 {

    private View target;
    private OnSwitchListener listener;

    private int swipeDistance;
    private int swipeTrigger;
    private static final float SWIPE_RADIO = 0.4F;

    private int pointerId;
    private float initialX;
    private float initialY;
    private int touchSlop;

    private boolean isBeingDragged = false;
    private boolean isBeingHorizontalDragged = false;
    private boolean isBeingNestedScrolling = false;

    public static final int DIRECTION_LEFT = -1;
    public static final int DIRECTION_RIGHT = 1;

    private static final String TAG = "SwipeSwitchLayout";

    private class ResetAnimation extends Animation {

        private float from;

        ResetAnimation(float from) {
            this.from = from;
            setInterpolator(new AccelerateDecelerateInterpolator());
            setDuration((long) (100.0 + 50.0 * Math.abs(swipeDistance) / swipeTrigger));
            setAnimationListener(animListener);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            swipeDistance = (int) (from * (1 - interpolatedTime));
            setTranslation();
        }
    }

    private class ExitAnimation extends Animation {
        // data
        private float[] froms;
        private float[] tos;

        private float realInterpolatedTime;
        private float lastInterpolatedTime;
        private boolean notified;
        private int direction;

        ExitAnimation(int direction, float from) {
            if (direction == DIRECTION_LEFT) {
                this.froms = new float[] {from, -swipeTrigger};
                this.tos = new float[] {from + swipeTrigger, 0};
            } else {
                this.froms = new float[] {from, swipeTrigger};
                this.tos = new float[] {from - swipeTrigger, 0};
            }

            this.realInterpolatedTime = 0;
            this.lastInterpolatedTime = 0;
            this.notified = false;
            this.direction = direction;

            setInterpolator(new AccelerateDecelerateInterpolator());
            setDuration(300);
            setAnimationListener(animListener);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            if (notified) {
                return;
            }
            if (interpolatedTime < 0.5) {
                // hide.
                realInterpolatedTime = interpolatedTime * 2;
                swipeDistance = (int) (froms[0] + (tos[0] - froms[0]) * realInterpolatedTime);
            } else {
                // show.
                if (lastInterpolatedTime < 0.5 && listener != null) {
                    listener.onSwitch(direction);
                }
                realInterpolatedTime = (interpolatedTime - 0.5F) * 2;
                swipeDistance = (int) (froms[1] + (tos[1] - froms[1]) * realInterpolatedTime);
            }
            setTranslation();
            target.setAlpha(interpolatedTime < 0.5 ? 1 - realInterpolatedTime : realInterpolatedTime);
            lastInterpolatedTime = interpolatedTime;
        }
    }

    private Animation.AnimationListener animListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setEnabled(true);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // do nothing.
        }
    };

    public static class RecyclerView extends androidx.recyclerview.widget.RecyclerView {

        private SwipeSwitchLayout switchView;

        public RecyclerView(Context context) {
            super(context);
            this.initialize();
        }

        public RecyclerView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.initialize();
        }

        public RecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.initialize();
        }

        private void initialize() {
            setNestedScrollingEnabled(false);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            ensureSwitchView(this);

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (switchView != null) {
                        switchView.setEnabled(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setEnabled(true);
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            ensureSwitchView(this);

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (switchView != null) {
                        switchView.setEnabled(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setEnabled(true);
                    }
                    break;
            }
            return super.onTouchEvent(ev);
        }

        private void ensureSwitchView(View v) {
            if (switchView == null) {
                ViewParent parent = v.getParent();
                if (parent != null) {
                    if (parent instanceof SwipeSwitchLayout) {
                        switchView = (SwipeSwitchLayout) parent;
                    } else {
                        ensureSwitchView((View) parent);
                    }
                }
            }
        }
    }

    public static class ViewPager extends androidx.viewpager.widget.ViewPager {

        private SwipeSwitchLayout switchView;

        public ViewPager(Context context) {
            super(context);
        }

        public ViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            ensureSwitchView(this);

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (switchView != null) {
                        switchView.setEnabled(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setEnabled(true);
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            ensureSwitchView(this);

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (switchView != null) {
                        switchView.setEnabled(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setEnabled(true);
                    }
                    break;
            }
            return super.onTouchEvent(ev);
        }

        private void ensureSwitchView(View v) {
            if (switchView == null) {
                ViewParent parent = v.getParent();
                if (parent != null) {
                    if (parent instanceof SwipeSwitchLayout) {
                        switchView = (SwipeSwitchLayout) parent;
                    } else {
                        ensureSwitchView((View) parent);
                    }
                }
            }
        }
    }

    public static class ViewPager2 extends ViewPager {

        public ViewPager2(Context context) {
            super(context);
        }

        public ViewPager2(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = getResources().getDimensionPixelSize(R.dimen.item_photo_3_more_vertical_height)
                    + MysplashApplication.getInstance().getWindowInsets().bottom;
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
        }
    }

    public SwipeSwitchLayout(Context context) {
        super(context);
        this.initialize();
    }

    public SwipeSwitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public SwipeSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    // init.

    private void initialize() {
        this.swipeDistance = 0;
        this.swipeTrigger = (int) (getContext().getResources().getDisplayMetrics().widthPixels / 3.0);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
    }

    // touch.

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()
                || (ev.getActionMasked() != MotionEvent.ACTION_DOWN && isBeingNestedScrolling)) {
            return false;
        }

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onDown(ev);
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                onPointerDown(ev);
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

                if (!isBeingDragged && !isBeingHorizontalDragged) {
                    if (Math.abs(x - initialX) > touchSlop || Math.abs(y - initialY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(x - initialX) > Math.abs(y - initialY)) {
                            initialX += x > initialX ? touchSlop : -touchSlop;
                            isBeingHorizontalDragged = true;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                onPointerUp(ev);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                isBeingHorizontalDragged = false;
                break;
        }

        return isBeingDragged && isBeingHorizontalDragged;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || isBeingNestedScrolling) {
            return false;
        }

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onDown(ev);
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                onPointerDown(ev);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(pointerId);
                if (index == -1) {
                    Log.e(TAG, "Invalid pointerId=" + pointerId + " in onTouchEvent");
                    break;
                }

                float x = ev.getX(index);

                if (isBeingDragged && isBeingHorizontalDragged) {
                    swipeDistance = (int) (x - initialX);
                    if (listener != null) {
                        setTranslation();
                    } else {
                        swipeDistance = 0;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                onPointerUp(ev);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                release();
                break;
        }

        return true;
    }

    // control.

    private void onDown(MotionEvent ev) {
        isBeingDragged = false;
        isBeingHorizontalDragged = false;
        isBeingNestedScrolling = false;

        pointerId = ev.getPointerId(0);
        initialX = ev.getX();
        initialY = ev.getY();

        swipeDistance = 0;
    }

    private void onPointerDown(MotionEvent ev) {
        int index = ev.getActionIndex();
        pointerId = ev.getPointerId(index);
        initialX = ev.getX(index);
        initialY = ev.getY(index);
    }

    private void onPointerUp(MotionEvent ev) {
        int index = ev.getActionIndex();
        int id = ev.getPointerId(index);
        if (pointerId == id) {
            int newIndex = index == 0 ? 1 : 0;

            this.pointerId = ev.getPointerId(newIndex);
            initialX = (int) ev.getX(newIndex);
            initialY = (int) ev.getY(newIndex);
        }
    }

    private void getTarget() {
        for (int i = 0; i < getChildCount(); i ++) {
            if (!(getChildAt(i) instanceof ImageView)) {
                target = getChildAt(i);
                return;
            }
        }
    }

    private void setTranslation() {
        if (target == null) {
            getTarget();
        }
        int dir = swipeDistance > 0 ? DIRECTION_LEFT : DIRECTION_RIGHT;
        if (listener != null) {
            listener.onSwipe(
                    dir,
                    (float) (1.0 * Math.min(swipeTrigger, Math.abs(swipeDistance)) / swipeTrigger)
            );
        }
        target.setTranslationX(
                (float) (
                        -dir * SWIPE_RADIO * swipeTrigger * Math.log10(
                                1 + 9.0 * Math.abs(swipeDistance) / swipeTrigger
                        )
                )
        );
    }

    private void release() {
        if (Math.abs(swipeDistance) > Math.abs(swipeTrigger)) {
            if (listener != null) {
                int dir = swipeDistance > 0 ? DIRECTION_LEFT : DIRECTION_RIGHT;
                if (listener.canSwitch(dir)) {
                    startAnimation(new ExitAnimation(dir, swipeDistance));
                } else {
                    startAnimation(new ResetAnimation(swipeDistance));
                }
            }
        } else if (swipeDistance != 0) {
            startAnimation(new ResetAnimation(swipeDistance));
        }
    }

    // interface.

    // on switch listener.

    public interface OnSwitchListener {
        void onSwipe(int direction, float progress);
        boolean canSwitch(int direction);
        void onSwitch(int direction);
    }

    public void setOnSwitchListener(OnSwitchListener l) {
        this.listener = l;
    }

    // nested scroll parent.

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0
                && type == ViewCompat.TYPE_TOUCH
                && isEnabled();
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        isBeingNestedScrolling = true;
        swipeDistance = 0;
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        isBeingNestedScrolling = false;
        release();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (swipeDistance != 0) {
            if ((swipeDistance > 0 && swipeDistance + dx < 0)
                    || (swipeDistance < 0 && swipeDistance + dx > 0)) {
                consumed[0] = -swipeDistance;
            } else {
                consumed[0] = dx;
            }
            onNestedScroll(target, 0, 0, consumed[0], dy, type);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type, @NonNull int[] consumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        consumed[0] += dxUnconsumed;
    }

    @Override
    public void onNestedScroll(@NonNull View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
                               int type) {
        swipeDistance += dxUnconsumed;
        if (listener != null) {
            setTranslation();
        } else {
            swipeDistance = 0;
        }
    }
}
