package com.wangdaye.mysplash._common.ui.widget.swipeRefreshView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/**
 * Both way swipe refresh layout.
 * */

public class BothWaySwipeRefreshLayout extends ViewGroup
        implements NestedScrollingParent, NestedScrollingChild {
    // direction
    public static final int DIRECTION_TOP = 0;
    public static final int DIRECTION_BOTTOM = 1;

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final int CIRCLE_DIAMETER = 40;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int SCALE_DOWN_DURATION = 150;

    private static final int ALPHA_ANIMATION_DURATION = 300;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private View mTarget; // the target of the gesture
    private OnRefreshAndLoadListener mListener;
    private boolean mRefreshing = false;
    private boolean mLoading = false;
    private boolean mPermitRefresh = true;
    private boolean mPermitLoad = true;
    private int mTouchSlop;
    private float mDragTriggerDistance = -1;

    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    private int mMediumAnimationDuration;
    private int mDragOffsetDistance;
    // Whether or not the starting offset has been determined.
    private boolean mOriginalOffsetCalculated = false;

    private float mInitialDownY;
    private boolean mIsBeingDragged;
    // Whether this item is scaled up rather than clipped.
    private boolean mScale = false;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };

    private CircleImageView[] mCircleViews;

    protected int mFrom;

    private float mStartingScale;

    private MaterialProgressDrawable[] mProgress;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mAlphaStartAnimation;

    private Animation mAlphaMaxAnimation;

    private Animation mScaleDownToStartAnimation;

    private boolean mNotify;

    private int mCircleWidth;
    private int mCircleHeight;

    /** <br> life cycle. */

    public BothWaySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public BothWaySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);
        mDragTriggerDistance = DEFAULT_CIRCLE_TARGET * metrics.density;

        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    private void createProgressView() {
        this.mCircleViews = new CircleImageView[] {
                new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2),
                new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2)
        };
        this.mProgress = new MaterialProgressDrawable[] {
                new MaterialProgressDrawable(getContext(), this),
                new MaterialProgressDrawable(getContext(), this)
        };

        for (int i = 0; i < 2; i ++) {
            mProgress[i].setBackgroundColor(CIRCLE_BG_LIGHT);
            mCircleViews[i].setImageDrawable(mProgress[i]);
            mCircleViews[i].setVisibility(View.GONE);
            addView(mCircleViews[i]);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    /** <br> UI. */

    // layout.

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget != null) {
            mTarget.measure(
                    View.MeasureSpec.makeMeasureSpec(
                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            View.MeasureSpec.EXACTLY));
        }

        for (int i = 0; i < 2; i ++) {
            mCircleViews[i].measure(
                    View.MeasureSpec.makeMeasureSpec(mCircleWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(mCircleHeight, View.MeasureSpec.EXACTLY));
        }
        if (!mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            mDragOffsetDistance = 0;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget != null) {
            final int childLeft = getPaddingLeft();
            final int childTop = getPaddingTop();
            final int childWidth = width - getPaddingLeft() - getPaddingRight();
            final int childHeight = height - getPaddingTop() - getPaddingBottom();
            mTarget.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }

        if (mDragOffsetDistance == 0) {
            mCircleViews[0].layout(
                    (width / 2 - mCircleWidth / 2),
                    -mCircleHeight,
                    (width / 2 + mCircleWidth / 2),
                    0);
            mCircleViews[1].layout(
                    (width / 2 - mCircleWidth / 2),
                    getMeasuredHeight(),
                    (width / 2 + mCircleWidth / 2),
                    getMeasuredHeight() + mCircleHeight);
        } else if(mDragOffsetDistance > 0) {
            mCircleViews[0].layout(
                    (width / 2 - mCircleWidth / 2),
                    mDragOffsetDistance - mCircleHeight,
                    (width / 2 + mCircleWidth / 2),
                    mDragOffsetDistance);
            mCircleViews[1].layout(
                    (width / 2 - mCircleWidth / 2),
                    getMeasuredHeight(),
                    (width / 2 + mCircleWidth / 2),
                    getMeasuredHeight() + mCircleHeight);
        } else if (mDragOffsetDistance < 0) {
            mCircleViews[0].layout(
                    (width / 2 - mCircleWidth / 2),
                    -mCircleHeight,
                    (width / 2 + mCircleWidth / 2),
                    0);
            mCircleViews[1].layout(
                    (width / 2 - mCircleWidth / 2),
                    getMeasuredHeight() + mDragOffsetDistance,
                    (width / 2 + mCircleWidth / 2),
                    getMeasuredHeight() + mCircleHeight + mDragOffsetDistance);
        }
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleViews[0]) && !child.equals(mCircleViews[1])) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    // position.

    private void moveSpinner(int dir, float dragDistance) {
        mProgress[dir].showArrow(true);
        float originalDragPercent = dragDistance / mDragTriggerDistance;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(dragDistance) - mDragTriggerDistance;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, mDragTriggerDistance * 2) / mDragTriggerDistance);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (mDragTriggerDistance) * tensionPercent * 2;

        int offset = (int) ((mDragTriggerDistance * dragPercent) + extraMove) * (dir == DIRECTION_TOP ? 1 : -1);

        // where 1.0f is a full circle
        if (mCircleViews[dir].getVisibility() != View.VISIBLE) {
            mCircleViews[dir].setVisibility(View.VISIBLE);
        }

        if (!mScale) {
            ViewCompat.setScaleX(mCircleViews[dir], 1f);
            ViewCompat.setScaleY(mCircleViews[dir], 1f);
        }

        if (mScale) {
            setAnimationProgress(dir, Math.min(1f, Math.abs(dragDistance / mDragTriggerDistance)));
        }
        if (dragDistance < mDragTriggerDistance) {
            if (mProgress[dir].getAlpha() > STARTING_PROGRESS_ALPHA
                    && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation(dir);
            }
        } else {
            if (mProgress[dir].getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation(dir);
            }
        }
        float strokeStart = adjustedPercent * .8f;
        mProgress[dir].setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress[dir].setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress[dir].setProgressRotation(rotation);
        setTargetOffsetTopAndBottom(dir, offset - mDragOffsetDistance);
    }

    private void finishSpinner(final int dir, float dragDistance) {
        if (dragDistance > mDragTriggerDistance) {
            if (dir == DIRECTION_TOP) {
                setRefreshing(true, true /* notify */);
            } else {
                setLoading(true, true);
            }
        } else {
            // cancel refresh
            if (dir == DIRECTION_TOP) {
                mRefreshing = false;
            } else {
                mLoading = false;
            }
            mProgress[dir].setStartEndTrim(0f, 0f);
            Animation.AnimationListener listener = null;
            if (!mScale) {
                listener = new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!mScale) {
                            startScaleDownAnimation(dir, null);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                };
            }
            animateOffsetToStartPosition(dir, mDragOffsetDistance, listener);
            mProgress[dir].showArrow(false);
        }
    }

    private void moveToStart(int dir, float interpolatedTime) {
        int offset = (int) (mFrom * (1 - interpolatedTime));
        setTargetOffsetTopAndBottom(dir, offset - mDragOffsetDistance);
    }

    private void setTargetOffsetTopAndBottom(int dir, int offset) {
        mCircleViews[dir].bringToFront();
        mCircleViews[dir].offsetTopAndBottom(offset);
        mDragOffsetDistance += offset;
    }

    private void reset() {
        int oldOffset = mDragOffsetDistance;
        for (int i = 0; i < 2; i ++) {
            mCircleViews[i].clearAnimation();
            mProgress[i].stop();
            mCircleViews[i].setVisibility(View.GONE);
            setColorViewAlpha(i, MAX_ALPHA);
            // Return the circle to its start position
            if (mScale) {
                setAnimationProgress(i, 0 /* animation complete and view is hidden */);
            } else {
                setTargetOffsetTopAndBottom(i, -oldOffset);
            }
        }
        mDragOffsetDistance = 0;
    }

    // state.

    public void setRefreshing(boolean refreshing) {
        if (refreshing && (mRefreshing || mLoading)) {
            return;
        }
        if (refreshing) {
            mCircleViews[DIRECTION_BOTTOM].setVisibility(GONE);
            // scale and show
            mRefreshing = true;
            setTargetOffsetTopAndBottom(DIRECTION_TOP, (int) (mDragTriggerDistance - mDragOffsetDistance));
            mNotify = false;
            startScaleUpAnimation(DIRECTION_TOP, mRefreshListener);
        } else {
            setRefreshing(false, false /* notify */);
        }
    }

    public void setLoading(boolean loading) {
        if (loading && (mRefreshing || mLoading)) {
            return;
        }
        if (loading) {
            mCircleViews[DIRECTION_TOP].setVisibility(GONE);
            // scale and show
            mLoading = true;
            setTargetOffsetTopAndBottom(DIRECTION_BOTTOM, (int) (-mDragTriggerDistance - mDragOffsetDistance));
            mNotify = false;
            startScaleUpAnimation(DIRECTION_BOTTOM, mLoadListener);
        } else {
            setLoading(false, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (refreshing && (mRefreshing || mLoading)) {
            return;
        }
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(DIRECTION_TOP, mDragOffsetDistance, mRefreshListener);
            } else {
                startScaleDownAnimation(DIRECTION_TOP, mRefreshListener);
            }
        }
    }

    private void setLoading(boolean loading, final boolean notify) {
        if (loading && (mRefreshing || mLoading)) {
            return;
        }
        if (mLoading != loading) {
            mNotify = notify;
            ensureTarget();
            mLoading = loading;
            if (mLoading) {
                animateOffsetToCorrectPosition(DIRECTION_BOTTOM, mDragOffsetDistance, mLoadListener);
            } else {
                startScaleDownAnimation(DIRECTION_BOTTOM, mLoadListener);
            }
        }
    }

    // color.

    private void setColorViewAlpha(int dir, int targetAlpha) {
        mCircleViews[dir].getBackground().setAlpha(targetAlpha);
        mProgress[dir].setAlpha(targetAlpha);
    }

    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
    }

    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        for (int i = 0; i < 2; i ++) {
            mCircleViews[i].setBackgroundColor(color);
            mProgress[i].setBackgroundColor(color);
        }
    }

    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = ContextCompat.getColor(getContext(), colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    @SuppressLint("SupportAnnotationUsage")
    @ColorInt
    public void setColorSchemeColors(int... colors) {
        ensureTarget();
        for (int i = 0; i < 2; i ++) {
            mProgress[i].setColorSchemeColors(colors);
        }
    }

    /** <br> touch. */

    // touch event.

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart
                || mNestedScrollInProgress || mRefreshing || mLoading) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldOffset = mDragOffsetDistance;
                for (int i = 0; i < 2; i ++) {
                    setTargetOffsetTopAndBottom(i, -oldOffset);
                }

                mIsBeingDragged = false;
                final float initialDownY = ev.getY();
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                final float yDiff = ev.getY() - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged && !canChildScrollUp() && mPermitRefresh) {
                    mIsBeingDragged = true;
                    mProgress[DIRECTION_TOP].setAlpha(STARTING_PROGRESS_ALPHA);
                } else if (yDiff < -mTouchSlop && !mIsBeingDragged && !canChildScrollDown() && mPermitLoad) {
                    mIsBeingDragged = true;
                    mProgress[DIRECTION_BOTTOM].setAlpha(STARTING_PROGRESS_ALPHA);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart
                || mNestedScrollInProgress || mRefreshing || mLoading) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                final float y = ev.getY();
                final float offset = (y - mInitialDownY) * DRAG_RATE;
                if (mIsBeingDragged) {
                    if (offset > 0 && !canChildScrollUp()) {
                        moveSpinner(DIRECTION_TOP, offset);
                    } else if (offset < 0 && !canChildScrollDown()) {
                        moveSpinner(DIRECTION_BOTTOM, offset);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                final float y = ev.getY();
                final float offset = (y - mInitialDownY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (offset > 0 && !canChildScrollUp()) {
                    finishSpinner(DIRECTION_TOP, offset);
                } else if (offset < 0 && !canChildScrollDown()) {
                    finishSpinner(DIRECTION_BOTTOM, offset);
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // do nothing.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // nested scroll parent.

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled()
                && !mReturningToStart && !mRefreshing && !mLoading
                && (mPermitRefresh || mPermitLoad)
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(DIRECTION_TOP, mTotalUnconsumed);
        } else if (dy < 0 && mTotalUnconsumed < 0) {
            if (dy < mTotalUnconsumed) {
                consumed[1] = (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            moveSpinner(DIRECTION_BOTTOM, mTotalUnconsumed);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp() && !mRefreshing && mPermitRefresh) {
            mTotalUnconsumed -= dy;
            moveSpinner(DIRECTION_TOP, mTotalUnconsumed);
        } else if (dy > 0 && !canChildScrollDown() && !mLoading && mPermitLoad) {
            mTotalUnconsumed -= dy;
            moveSpinner(DIRECTION_BOTTOM, mTotalUnconsumed);
        }
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(DIRECTION_TOP, mTotalUnconsumed);
        } else if (mTotalUnconsumed < 0) {
            finishSpinner(DIRECTION_BOTTOM, mTotalUnconsumed);
        }
        mTotalUnconsumed = 0;
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    // nested scrolling child.

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    /** <br> data. */

    public boolean isRefreshing() {
        return mRefreshing;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setPermitRefresh(boolean permit) {
        mPermitRefresh = permit;
        if (!mPermitRefresh && !mPermitLoad) {
            setEnabled(false);
        }
    }

    public void setPermitLoad(boolean permit) {
        mPermitLoad = permit;
        if (!mPermitRefresh && !mPermitLoad) {
            setEnabled(false);
        }
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mTarget, -1);
    }

    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mTarget, 1);
    }

    /** <br> animation. */

    // to correct position.

    private void animateOffsetToCorrectPosition(int dir, int from, Animation.AnimationListener listener) {
        mFrom = from;
        if (dir == DIRECTION_TOP) {
            mAnimateToTopCorrectPosition.reset();
            mAnimateToTopCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
            mAnimateToTopCorrectPosition.setInterpolator(mDecelerateInterpolator);
        } else {
            mAnimateToBottomCorrectPosition.reset();
            mAnimateToBottomCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
            mAnimateToBottomCorrectPosition.setInterpolator(mDecelerateInterpolator);
        }
        if (listener != null) {
            mCircleViews[dir].setAnimationListener(listener);
        }
        mCircleViews[dir].clearAnimation();
        mCircleViews[dir].startAnimation(
                dir == DIRECTION_TOP ? mAnimateToTopCorrectPosition : mAnimateToBottomCorrectPosition);
    }

    private final Animation mAnimateToTopCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            setTargetOffsetTopAndBottom(
                    DIRECTION_TOP,
                    (int) (mFrom + (mDragTriggerDistance - mFrom) * interpolatedTime - mDragOffsetDistance));
            mProgress[DIRECTION_TOP].setArrowScale(1 - interpolatedTime);
        }
    };

    private final Animation mAnimateToBottomCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            setTargetOffsetTopAndBottom(
                    DIRECTION_BOTTOM,
                    (int) (mFrom + (-mDragTriggerDistance - mFrom) * interpolatedTime - mDragOffsetDistance));
            mProgress[DIRECTION_BOTTOM].setArrowScale(1 - interpolatedTime);
        }
    };

    // to start position.

    private void animateOffsetToStartPosition(int dir, int from, Animation.AnimationListener listener) {
        if (mScale) {
            // Scale the item back down
            if (dir == DIRECTION_TOP) {
                startScaleDownReturnToTopStartAnimation(from, listener);
            } else {
                startScaleDownReturnToBottomStartAnimation(from, listener);
            }
        } else {
            mFrom = from;
            if (dir == DIRECTION_TOP) {
                mAnimateToTopStartPosition.reset();
                mAnimateToTopStartPosition.setDuration(ANIMATE_TO_START_DURATION);
                mAnimateToTopStartPosition.setInterpolator(mDecelerateInterpolator);
            } else {
                mAnimateToBottomStartPosition.reset();
                mAnimateToBottomStartPosition.setDuration(ANIMATE_TO_START_DURATION);
                mAnimateToBottomStartPosition.setInterpolator(mDecelerateInterpolator);
            }
            if (listener != null) {
                mCircleViews[dir].setAnimationListener(listener);
            }
            mCircleViews[dir].clearAnimation();
            mCircleViews[dir].startAnimation(
                    dir == DIRECTION_TOP ? mAnimateToTopStartPosition : mAnimateToBottomStartPosition);
        }
    }

    private final Animation mAnimateToTopStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(DIRECTION_TOP, interpolatedTime);
        }
    };

    private final Animation mAnimateToBottomStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(DIRECTION_BOTTOM, interpolatedTime);
        }
    };

    private void startScaleDownReturnToTopStartAnimation(int from,
                                                         Animation.AnimationListener listener) {
        mFrom = from;
        mStartingScale = ViewCompat.getScaleX(mCircleViews[DIRECTION_TOP]);
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale  * interpolatedTime));
                setAnimationProgress(DIRECTION_TOP, targetScale);
                moveToStart(DIRECTION_TOP, interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mCircleViews[DIRECTION_TOP].setAnimationListener(listener);
        }
        mCircleViews[DIRECTION_TOP].clearAnimation();
        mCircleViews[DIRECTION_TOP].startAnimation(mScaleDownToStartAnimation);
    }

    private void startScaleDownReturnToBottomStartAnimation(int from,
                                                            Animation.AnimationListener listener) {
        mFrom = from;
        mStartingScale = ViewCompat.getScaleX(mCircleViews[DIRECTION_BOTTOM]);
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale  * interpolatedTime));
                setAnimationProgress(DIRECTION_BOTTOM, targetScale);
                moveToStart(DIRECTION_BOTTOM, interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mCircleViews[DIRECTION_BOTTOM].setAnimationListener(listener);
        }
        mCircleViews[DIRECTION_BOTTOM].clearAnimation();
        mCircleViews[DIRECTION_BOTTOM].startAnimation(mScaleDownToStartAnimation);
    }

    private void startScaleUpAnimation(final int dir, Animation.AnimationListener listener) {
        mCircleViews[dir].setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // Pre API 11, alpha is used in place of scale up to show the
            // progress circle appearing.
            // Don't adjust the alpha during appearance otherwise.
            mProgress[dir].setAlpha(MAX_ALPHA);
        }
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(dir, interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mCircleViews[dir].setAnimationListener(listener);
        }
        mCircleViews[dir].clearAnimation();
        mCircleViews[dir].startAnimation(mScaleAnimation);
    }

    private void setAnimationProgress(int dir, float progress) {
        ViewCompat.setScaleX(mCircleViews[dir], progress);
        ViewCompat.setScaleY(mCircleViews[dir], progress);
    }

    private void startScaleDownAnimation(final int dir, Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(dir, 1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mCircleViews[dir].setAnimationListener(listener);
        mCircleViews[dir].clearAnimation();
        mCircleViews[dir].startAnimation(mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation(int dir) {
        mAlphaStartAnimation = startAlphaAnimation(dir, mProgress[dir].getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation(int dir) {
        mAlphaMaxAnimation = startAlphaAnimation(dir, mProgress[dir].getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int dir, final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress[dir].setAlpha((int) (startingAlpha+ ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mCircleViews[dir].setAnimationListener(null);
        mCircleViews[dir].clearAnimation();
        mCircleViews[dir].startAnimation(alpha);
        return alpha;
    }

    /** <br> interface. */

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress[DIRECTION_TOP].setAlpha(MAX_ALPHA);
                mProgress[DIRECTION_TOP].start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mDragOffsetDistance = (int) mDragTriggerDistance;
            } else {
                reset();
            }
        }
    };

    private Animation.AnimationListener mLoadListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mLoading) {
                // Make sure the progress view is fully visible
                mProgress[DIRECTION_BOTTOM].setAlpha(MAX_ALPHA);
                mProgress[DIRECTION_BOTTOM].start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onLoad();
                    }
                }
                mDragOffsetDistance = (int) -mDragTriggerDistance;
            } else {
                reset();
            }
        }
    };

    // on refresh and load listener.

    public interface OnRefreshAndLoadListener {
        void onRefresh();
        void onLoad();
    }

    public void setOnRefreshAndLoadListener(OnRefreshAndLoadListener listener) {
        mListener = listener;
    }
}