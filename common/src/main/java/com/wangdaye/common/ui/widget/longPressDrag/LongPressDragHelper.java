package com.wangdaye.common.ui.widget.longPressDrag;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.wangdaye.common.R;
import com.wangdaye.common.utils.DisplayUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class LongPressDragHelper {

    @NonNull private View parent;
    @NonNull private List<View> childList;
    @Nullable private View.OnClickListener clickListener;

    private Paint paint;
    private int cancelFlagBaseLine;

    @Nullable private Animator showAnimator;
    @Nullable private Object showTarget;
    @Nullable private Animator hideAnimator;
    @Nullable private Object hideTarget;

    private boolean longPressed;
    private boolean dragging;

    private int initX;
    private int initY;
    private int touchSlop;

    private int dragRange;
    private int initDragDistance;
    private int dragIndex;

    private int cancelRange;
    private boolean canceled;

    private static final long LONG_TOUCH_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int INVALID_DRAG_INDEX = -1;
    private static final float SELECTED_SCALE = 1.3f;
    private static final long ANIMATOR_DURATION = 150;

    public LongPressDragHelper(@NonNull View parent) {
        this(parent, new ArrayList<>());
    }

    public LongPressDragHelper(@NonNull View parent, @NonNull List<View> childList) {
        if (!(parent instanceof LongPressDragView)) {
            throw new InvalidParameterException("Parent view must be a LongPressDragView.");
        }

        this.init();

        this.parent = parent;
        this.childList = childList;

        this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(parent.getResources().getDimensionPixelSize(R.dimen.mini_icon_size));
        paint.setTextAlign(Paint.Align.CENTER);

        this.cancelFlagBaseLine = (int) (parent.getResources().getDimensionPixelSize(R.dimen.normal_margin)
                - paint.getFontMetrics().top);

        this.touchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        this.dragRange = DisplayUtils.getScreenSize(parent.getContext())[0] / 6;
        this.cancelRange = dragRange / 3;
    }

    public void setChildList(@NonNull List<View> childList) {
        this.childList = childList;
    }

    public void setOnClickListener(@Nullable View.OnClickListener l) {
        clickListener = l;
    }

    public void setCancelFlagMarginTop(@Px int marginTop) {
        this.cancelFlagBaseLine = (int) (marginTop - paint.getFontMetrics().top);
    }

    public void init() {
        longPressed = false;
        dragging = false;
        canceled = false;
    }

    public boolean isLongPressed() {
        return longPressed;
    }

    public boolean isDragging() {
        return dragging;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void draw(Canvas canvas) {
        if (longPressed) {
            paint.setColor(Color.WHITE);
            if (canceled) {
                paint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                paint.setTypeface(Typeface.DEFAULT);
            }
            canvas.drawText("×", parent.getMeasuredWidth() / 2f, cancelFlagBaseLine, paint);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (childList.size() == 0) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                init();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                init();
                break;

        }
        return longPressed;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (childList.size() == 0) {
            return false;
        }

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                init();
                initX = x;
                initY = y;
                ((LongPressDragView) parent).innerSetOnClickListener(clickListener);
                return false;

            case MotionEvent.ACTION_MOVE:
                boolean vibrated = false;

                if (!longPressed) {
                    if (dragging) {
                        // if dragging, do not consume the touch event.
                        return false;
                    }
                    if (ev.getEventTime() - ev.getDownTime() >= LONG_TOUCH_TIMEOUT) {
                        longPressed = true;
                        initDragDistance = (int) (1.0 * initX / parent.getMeasuredWidth() * dragRange);
                        dragIndex = INVALID_DRAG_INDEX;

                        vibrated = true;
                        parent.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        parent.playSoundEffect(SoundEffectConstants.CLICK);
                        parent.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (Math.abs(initX - x) > touchSlop || Math.abs(initY - y) > touchSlop) {
                        dragging = true;
                    }
                }
                if (longPressed) {
                    int dragDistance = (x - initX) + initDragDistance;
                    dragDistance = Math.max(dragDistance, 0);
                    dragDistance = Math.min(dragDistance, dragRange);

                    canceled = initY - y > cancelRange;

                    int newDragIndex = canceled
                            ? INVALID_DRAG_INDEX
                            : (int) (1.0 * dragDistance / dragRange * (childList.size() - 1));
                    if (newDragIndex != dragIndex) {
                        if (newDragIndex != INVALID_DRAG_INDEX) {
                            show(childList.get(newDragIndex));
                        }
                        if (dragIndex != INVALID_DRAG_INDEX) {
                            hide(childList.get(dragIndex));
                        }
                        if (!vibrated) {
                            parent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        }

                        dragIndex = newDragIndex;
                    }
                }
                return longPressed;

            case MotionEvent.ACTION_UP:
                if (longPressed && dragIndex != INVALID_DRAG_INDEX) {
                    hide(childList.get(dragIndex));
                    ((LongPressDragView) parent).innerSetOnClickListener(null);
                    childList.get(dragIndex).performClick();
                } else if (longPressed || dragging) {
                    ((LongPressDragView) parent).innerSetOnClickListener(null);
                }
            case MotionEvent.ACTION_CANCEL:
                init();
                parent.getParent().requestDisallowInterceptTouchEvent(false);
                return true;

        }
        return false;
    }

    private void show(View child) {
        if (hideAnimator != null && hideTarget == child) {
            hideAnimator.cancel();
        }
        if (child.getScaleX() == SELECTED_SCALE && child.getScaleY() == SELECTED_SCALE) {
            return;
        }
        if (showAnimator == null) {
            showAnimator = new AnimatorSet();
            ((AnimatorSet) showAnimator).playTogether(
                    ObjectAnimator.ofFloat(
                            child, "scaleX", child.getScaleX(), SELECTED_SCALE),
                    ObjectAnimator.ofFloat(
                            child, "scaleY", child.getScaleY(), SELECTED_SCALE)
            );
            showAnimator.setDuration(ANIMATOR_DURATION);
            showAnimator.setInterpolator(new FastOutSlowInInterpolator());
            showAnimator.setTarget(child);
        } else {
            showAnimator = showAnimator.clone();
            showAnimator.setTarget(child);
        }
        showTarget = child;
        showAnimator.start();
    }

    private void hide(View child) {
        if (showAnimator != null && showTarget == child) {
            showAnimator.cancel();
        }
        if (child.getScaleX() == 1 && child.getScaleY() == 1) {
            return;
        }

        if (hideAnimator == null) {
            hideAnimator = new AnimatorSet();
            ((AnimatorSet) hideAnimator).playTogether(
                    ObjectAnimator.ofFloat(
                            child, "scaleX", child.getScaleX(), 1),
                    ObjectAnimator.ofFloat(
                            child, "scaleY", child.getScaleY(), 1)
            );
            hideAnimator.setDuration(ANIMATOR_DURATION);
            hideAnimator.setInterpolator(new FastOutSlowInInterpolator());
            hideAnimator.setTarget(child);
        } else {
            hideAnimator = hideAnimator.clone();
            hideAnimator.setTarget(child);
        }
        hideTarget = child;
        hideAnimator.start();
    }
}
