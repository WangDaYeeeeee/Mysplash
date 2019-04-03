package com.wangdaye.mysplash.common.ui.widget.singleOrientationScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.EdgeEffectCompat;

public class ScalableImageView extends AppCompatImageView
        implements NestedScrollingChild3 {

    private OverScroller overScroller;
    private EdgeEffect edgeGlowLeft;
    private EdgeEffect edgeGlowRight;
    private NestedScrollingChildHelper nestedScrollingChildHelper;

    @Nullable private VelocityTracker velocityTracker;
    @Nullable private OnClickListener clickListener;

    private Matrix imageMatrix;
    private float[] matrixValues;

    private int translateX;
    private int maxTranslateX;
    private int minTranslateX;

    private int pointerId;
    private float lastX;
    private float lastY;

    private int touchSlop;
    private int maximumFlingVelocity;
    private int minimumFlingVelocity;

    private int[] scrollConsumed;
    private int[] scrollOffsetInWindow;
    private int nestedScrollingOffsetX;

    private boolean isBeingDragged = false;
    private boolean isHorizontalDragged = false;

    private float viewWidth;
    private float viewHeight;

    private static final int OVER_FLING = 0;

    private static final String TAG = "ScalableImageView";

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageMatrix = new Matrix();
        matrixValues = new float[9];

        int[] size = DisplayUtils.getScreenSize(context);
        viewWidth = size[0];
        viewHeight = size[1];

        overScroller = new OverScroller(context);
        overScroller.forceFinished(true);

        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        nestedScrollingChildHelper.setNestedScrollingEnabled(true);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
        maximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        minimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();

        scrollConsumed = new int[2];
        scrollOffsetInWindow = new int[2];

        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) viewWidth, (int) viewHeight);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            nestedScrollingOffsetX = 0;
        }
        ev.offsetLocation(nestedScrollingOffsetX, 0);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                isHorizontalDragged = false;

                pointerId = ev.getPointerId(0);
                lastX = ev.getX();
                lastY = ev.getY();

                cancelFling();
                getImageState();
                initOrResetVelocityTracker();

                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_TOUCH);
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

                float x = ev.getX(index);
                float y = ev.getY(index);

                if (!isBeingDragged && !isHorizontalDragged) {
                    if (Math.abs(x - lastX) > touchSlop || Math.abs(y - lastY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(x - lastX) > Math.abs(y - lastY)) {
                            lastX += x > lastX ? touchSlop : -touchSlop;

                            isHorizontalDragged = true;
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }

                if (isBeingDragged && isHorizontalDragged) {
                    int distanceX = (int) (x - lastX);
                    scrollConsumed[0] = scrollConsumed[1] = 0;

                    dispatchNestedPreScroll(
                            distanceX, 0, scrollConsumed, scrollOffsetInWindow, ViewCompat.TYPE_TOUCH);
                    nestedScrollingOffsetX += scrollOffsetInWindow[0];

                    int translateXTo = distanceX - scrollConsumed[0] + translateX;
                    int translateDx = setTranslation(translateXTo, false);

                    dispatchNestedScroll(
                            scrollConsumed[0] + translateDx, 0,
                            distanceX - scrollConsumed[0] - translateDx, 0,
                            scrollOffsetInWindow, ViewCompat.TYPE_TOUCH, scrollConsumed
                    );
                    nestedScrollingOffsetX += scrollOffsetInWindow[0];

                    if (scrollConsumed[0] == 0) {
                        ensureGlows();
                        if (translateXTo < minTranslateX) {
                            EdgeEffectCompat.onPull(
                                    edgeGlowLeft,
                                    1.0f * distanceX / getWidth(),
                                    1.0f * y / getHeight()
                            );
                            if (!edgeGlowRight.isFinished()) {
                                edgeGlowRight.onRelease();
                            }
                        } else if (translateXTo > maxTranslateX) {
                            EdgeEffectCompat.onPull(
                                    edgeGlowRight,
                                    1.0f * distanceX / getWidth(),
                                    1 - 1.0f * y / getHeight()
                            );
                            if (!edgeGlowLeft.isFinished()) {
                                edgeGlowLeft.onRelease();
                            }
                        }
                        if (edgeGlowLeft != null && edgeGlowRight != null
                                && (!edgeGlowLeft.isFinished() || !edgeGlowRight.isFinished())) {
                            ViewCompat.postInvalidateOnAnimation(this);
                        }
                    }

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
                if (clickListener != null && !isBeingDragged) {
                    clickListener.onClick(this);
                }
            case MotionEvent.ACTION_CANCEL:
                ensureGlows();
                edgeGlowLeft.onRelease();
                edgeGlowRight.onRelease();
                if (edgeGlowLeft.isFinished() || edgeGlowRight.isFinished()) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }

                if (isHorizontalDragged && velocityTracker != null) {
                    velocityTracker.computeCurrentVelocity(1000, maximumFlingVelocity);
                    float velocityX = velocityTracker.getXVelocity(ev.getPointerId(0));
                    if (Math.abs(velocityX) >= minimumFlingVelocity) {
                        fling(velocityX);
                    }
                }

                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                getParent().requestDisallowInterceptTouchEvent(false);

                isBeingDragged = false;
                isHorizontalDragged = false;
                break;
        }

        if (velocityTracker != null) {
            velocityTracker.addMovement(ev);
        }

        return true;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setScaleType(ScaleType.CENTER_CROP);
        super.setImageDrawable(drawable);
    }

    @Override
    public void computeScroll() {
        if (overScroller.computeScrollOffset()) {
            ensureGlows();
            if (edgeGlowLeft.isFinished()
                    && translateX > minTranslateX && overScroller.getCurrX() <= minTranslateX) {
                edgeGlowLeft.onAbsorb((int) overScroller.getCurrVelocity());
            }
            if (edgeGlowRight.isFinished()
                    && translateX < maxTranslateX && overScroller.getCurrX() >= maxTranslateX) {
                edgeGlowRight.onAbsorb((int) overScroller.getCurrVelocity());
            }

            setTranslation(overScroller.getCurrX(), true);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void getImageState() {
        setScaleType(ScaleType.MATRIX);

        imageMatrix.set(getImageMatrix());
        imageMatrix.getValues(matrixValues);

        translateX = (int) matrixValues[Matrix.MTRANS_X];

        maxTranslateX = 0;
        float drawableWidth = getDrawableWidth(getDrawable());
        if (drawableWidth > 0) {
            minTranslateX = (int) (viewWidth - drawableWidth * matrixValues[Matrix.MSCALE_X]);
        } else {
            minTranslateX = 0;
        }
    }

    private void cancelFling() {
        overScroller.forceFinished(true);
    }

    private void initOrResetVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void ensureGlows() {
        if (edgeGlowLeft == null) {
            edgeGlowLeft = new EdgeEffect(getContext());
        }
        if (edgeGlowRight == null) {
            edgeGlowRight = new EdgeEffect(getContext());
        }
    }

    /**
     * @return delta translation value (consumed scroll distance).
     * */
    private int setTranslation(int newTranslateX, boolean overScrollEnabled) {
        if (newTranslateX == translateX) {
            return 0;
        }

        if (!overScrollEnabled) {
            newTranslateX = Math.max(minTranslateX, newTranslateX);
            newTranslateX = Math.min(maxTranslateX, newTranslateX);
        }

        int consumed = newTranslateX - translateX;
        translateX = newTranslateX;

        imageMatrix.set(getImageMatrix());
        imageMatrix.postTranslate(consumed, 0);
        setImageMatrix(imageMatrix);

        return consumed;
    }

    private void fling(float velocityX) {
        overScroller.fling(
                translateX, 0,
                (int) velocityX, 0,
                minTranslateX, maxTranslateX, 0, 0,
                OVER_FLING, 0);
    }

    private static int getDrawableWidth(@Nullable Drawable d) {
        if (d == null) {
            return 0;
        }
        int width = d.getIntrinsicWidth();
        if (width <= 0) width = d.getMinimumWidth();
        if (width <= 0) width = d.getBounds().width();
        return width;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (edgeGlowLeft != null && edgeGlowRight != null) {
            int width = getWidth();
            int height = getHeight();

            if (!edgeGlowLeft.isFinished()) {
                int restoreCount = canvas.save();

                canvas.rotate(90);
                canvas.translate(0, -width);

                edgeGlowLeft.setSize(height, width);
                if (edgeGlowLeft.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }

                canvas.restoreToCount(restoreCount);
            }
            if (!edgeGlowRight.isFinished()) {
                int restoreCount = canvas.save();

                canvas.rotate(270);
                canvas.translate(-height, 0);

                edgeGlowRight.setSize(height, width);
                if (edgeGlowRight.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }

                canvas.restoreToCount(restoreCount);
            }
        }
    }

    // interface.

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        clickListener = l;
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
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed,
                                        @Nullable int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                     int dxUnconsumed, int dyUnconsumed,
                                     @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow, int type) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow, type);
    }
}
