package com.wangdaye.mysplash.common.ui.widget.horizontalScrollView;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Swipe switch layout.
 * */

public class SwipeSwitchLayout extends FrameLayout {

    private View target;
    private OnSwitchListener listener;

    @Nullable private ScalableImageView scalableView;
    @Nullable private VelocityTracker velocityTracker;

    private float dX;
    private float swipeDistance;
    private float swipeTrigger;
    private static final float SWIPE_RADIO = 0.4F;

    private float initialX, initialY;
    private int touchSlop;
    private int maximumFlingVelocity;
    private int minimumFlingVelocity;

    private boolean isBeingDragged = false;
    private boolean isHorizontalDragged = false;

    public static final int DIRECTION_LEFT = -1;
    public static final int DIRECTION_RIGHT = 1;

    private boolean usable;
    private boolean nestedScrollEnable;

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
                swipeDistance = froms[0] + (tos[0] - froms[0]) * realInterpolatedTime;
            } else {
                // show.
                if (lastInterpolatedTime < 0.5 && listener != null) {
                    listener.onSwitch(direction);
                }
                realInterpolatedTime = (interpolatedTime - 0.5F) * 2;
                swipeDistance = froms[1] + (tos[1] - froms[1]) * realInterpolatedTime;
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
                        switchView.setUsable(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setUsable(true);
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
                        switchView.setUsable(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setUsable(true);
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
                        switchView.setUsable(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setUsable(true);
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
                        switchView.setUsable(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchView != null) {
                        switchView.setUsable(true);
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
                    + DisplayUtils.getNavigationBarHeight(getResources());
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
        this.dX = 0;
        this.swipeDistance = 0;
        this.swipeTrigger = (float) (getContext().getResources().getDisplayMetrics().widthPixels / 3.0);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
        maximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        minimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();

        setUsable(true);
        setNestedScrollEnable(true);
    }

    // touch.

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                isHorizontalDragged = false;
                initialX = ev.getX();
                initialY = ev.getY();
                dX = 0;
                swipeDistance = 0;

                if (isNestedScrollEnable() && scalableView != null) {
                    scalableView.cancelFling();
                    scalableView.getImageState();
                }

                if (velocityTracker != null) {
                    velocityTracker.clear();
                    velocityTracker = null;
                }
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isBeingDragged && !isHorizontalDragged) {
                    if (Math.abs(ev.getX() - initialX) > touchSlop
                            || Math.abs(ev.getY() - initialY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(ev.getX() - initialX) > Math.abs(ev.getY() - initialY)) {
                            isHorizontalDragged = true;
                            if (velocityTracker == null) {
                                velocityTracker = VelocityTracker.obtain();
                            }
                            velocityTracker.addMovement(ev);
                        }
                    } else {
                        initialX = ev.getX();
                        initialY = ev.getY();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                isHorizontalDragged = false;
                break;
        }

        return isUsable() && isBeingDragged && isHorizontalDragged;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isBeingDragged && isHorizontalDragged) {
                    dX = ev.getX() - initialX;
                    float consumed = 0;
                    if (isNestedScrollEnable() && scalableView != null) {
                        consumed = scalableView.setTranslation(dX, false);
                    }
                    swipeDistance = dX - consumed;
                    if (listener != null) {
                        setTranslation();
                    } else {
                        swipeDistance = 0;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!release() && isNestedScrollEnable() && scalableView != null) {
                    velocityTracker.computeCurrentVelocity(1000, maximumFlingVelocity);
                    float velocityX = velocityTracker.getXVelocity(ev.getPointerId(0));
                    if (Math.abs(velocityX) >= minimumFlingVelocity) {
                        scalableView.fling(dX, velocityX);
                    }
                }
                if (velocityTracker != null) {
                    velocityTracker.clear();
                    velocityTracker = null;
                }
                break;
        }

        return isUsable();
    }

    // control.

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
                    (float) (1.0 * Math.min(swipeTrigger, Math.abs(swipeDistance)) / swipeTrigger));
        }
        target.setTranslationX(
                (float) (-1 * dir * SWIPE_RADIO * swipeTrigger
                        * Math.log10(1 + 9.0 * Math.abs(swipeDistance) / swipeTrigger)));
    }

    /**
     * @return release to switch succeed.
     * */
    private boolean release() {
        if (Math.abs(swipeDistance) > Math.abs(swipeTrigger)) {
            if (listener != null) {
                int dir = swipeDistance > 0 ? DIRECTION_LEFT : DIRECTION_RIGHT;
                if (listener.canSwitch(dir)) {
                    startAnimation(new ExitAnimation(dir, swipeDistance));
                } else {
                    startAnimation(new ResetAnimation(swipeDistance));
                }
                return true;
            }
        } else if (swipeDistance != 0) {
            startAnimation(new ResetAnimation(swipeDistance));
        }
        return false;
    }

    public void setScalableView(@Nullable ScalableImageView scalableView) {
        this.scalableView = scalableView;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public boolean isNestedScrollEnable() {
        return nestedScrollEnable;
    }

    public void setNestedScrollEnable(boolean nestedScrollEnable) {
        this.nestedScrollEnable = nestedScrollEnable;
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
}
