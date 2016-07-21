package com.wangdaye.mysplash.ui.widget.swipeRefreshLayout;

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
import android.util.Log;
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

    // Maps to ProgressBar.Large style
    public static final int LARGE = MaterialProgressDrawable.LARGE;
    // Maps to ProgressBar default style
    public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;

    private static final String LOG_TAG = BothWaySwipeRefreshLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
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
    private int mTouchSlop;
    private float mTotalDragDistance = -1;

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    private int mMediumAnimationDuration;
    private int mCurrentTargetOffsetTop;
    // Whether or not the starting offset has been determined.
    private boolean mOriginalOffsetCalculated = false;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    private boolean mScale;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };

    private CircleImageView[] mCircleViews;

    private int[] mCircleViewIndexes = new int[] {-1, -1};

    protected int mFrom;

    private float mStartingScale;

    protected int mOriginalOffsetTop;

    private MaterialProgressDrawable[] mProgress;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mAlphaStartAnimation;

    private Animation mAlphaMaxAnimation;

    private Animation mScaleDownToStartAnimation;

    private float mSpinnerFinalOffset;

    private boolean mNotify;

    private int mCircleWidth;

    private int mCircleHeight;

    // Whether the client has set a custom starting position;
    private boolean mUsingCustomStart;

    /** <br> life cycle. */

    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */
    public BothWaySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public BothWaySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);

        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
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
        reset(DIRECTION_TOP);
        reset(DIRECTION_BOTTOM);
    }

    /** <br> UI. */

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(View.MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), View.MeasureSpec.EXACTLY));
        for (int i = 0; i < 2; i ++) {
            mCircleViews[i].measure(View.MeasureSpec.makeMeasureSpec(mCircleWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(mCircleHeight, View.MeasureSpec.EXACTLY));
        }
        if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleViews[0].getMeasuredHeight();
        }

        for (int i = 0; i < 2; i ++) {
            mCircleViewIndexes[i] = -1;
            // Get the index of the circleview.
            for (int index = 0; index < getChildCount(); index++) {
                if (getChildAt(index) == mCircleViews[i]) {
                    mCircleViewIndexes[i] = index;
                }
            }
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
        if (mTarget == null) {
            return;
        }

        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        int circleWidth = mCircleViews[0].getMeasuredWidth();
        int circleHeight = mCircleViews[0].getMeasuredHeight();
        mCircleViews[0].layout(
                (width / 2 - circleWidth / 2),
                mCurrentTargetOffsetTop,
                (width / 2 + circleWidth / 2),
                mCurrentTargetOffsetTop + circleHeight);
        mCircleViews[1].layout(
                (width / 2 - circleWidth / 2),
                getMeasuredHeight() - mCurrentTargetOffsetTop - circleHeight,
                (width / 2 + circleWidth / 2),
                getMeasuredHeight() - mCurrentTargetOffsetTop);
    }

    private void moveSpinner(int dir, float overscrollTop) {
        mProgress[dir].showArrow(true); // mProgress[dir] 显示箭头
        float originalDragPercent = overscrollTop / mTotalDragDistance; // * 原始拖动比例 = 拖动长度 / 触发长度

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent)); // * 真实拖动比例 = [ 1 ]和[ 原始比例 ]中小的那一个
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3; // * 得到调整后的百分比
        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance; // * 获取多出的拖动距离
        float slingshotDist = mUsingCustomStart ?
                mSpinnerFinalOffset - mOriginalOffsetTop : mSpinnerFinalOffset; //  计算弹射距离
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) // 计算弹射的张力系数
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow( // 计算张力系数
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2; // 计算额外移动？

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove); // 计算CircleView[dir]在Y轴上的位置
        // where 1.0f is a full circle
        if (mCircleViews[dir].getVisibility() != View.VISIBLE) { // 令CircleView[dir]可见
            mCircleViews[dir].setVisibility(View.VISIBLE);
        }

        if (!mScale) { // 令CircleView[dir]变为完整大小
            ViewCompat.setScaleX(mCircleViews[dir], 1f);
            ViewCompat.setScaleY(mCircleViews[dir], 1f);
        }

        if (mScale) { // 显示progress[dir]
            setAnimationProgress(dir, Math.min(1f, overscrollTop / mTotalDragDistance));
        }
        if (overscrollTop < mTotalDragDistance) { // 拖动长度未能达到触发长度
            if (mProgress[dir].getAlpha() > STARTING_PROGRESS_ALPHA
                    && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation(dir);
            }
        } else { // 达到触发长度
            if (mProgress[dir].getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation(dir);
            }
        }
        float strokeStart = adjustedPercent * .8f; // 计算画笔开始的位置
        mProgress[dir].setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress[dir].setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f; // 计算循环数据
        mProgress[dir].setProgressRotation(rotation);
        setTargetOffsetTopAndBottom(dir, targetY - mCurrentTargetOffsetTop, true /* requires update */);
    }

    private void finishSpinner(final int dir, float overscrollTop) {
        if (overscrollTop > mTotalDragDistance) {
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
            animateOffsetToStartPosition(dir, mCurrentTargetOffsetTop, listener);
            mProgress[dir].showArrow(false);
        }
    }

    private void moveToStart(int dir, float interpolatedTime) {
        int targetTop;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = dir == DIRECTION_TOP ?
                targetTop - mCircleViews[dir].getTop()
                :
                targetTop - (getMeasuredHeight() - mCircleViews[dir].getTop() - mCircleHeight);
        setTargetOffsetTopAndBottom(dir, offset, false /* requires update */);
    }

    private void setTargetOffsetTopAndBottom(int dir, int offset, boolean requiresUpdate) {
        if (dir == DIRECTION_BOTTOM) {
            offset *= -1;
        }
        mCircleViews[dir].bringToFront();
        mCircleViews[dir].offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = dir == DIRECTION_TOP ?
                mCircleViews[dir].getTop() : getMeasuredHeight() - mCircleViews[dir].getTop() - mCircleHeight;
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    /** <br> touch. */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || mRefreshing || mLoading) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < 2; i ++) {
                    setTargetOffsetTopAndBottom(i, mOriginalOffsetTop - mCircleViews[i].getTop(), true);
                }
                if (!canChildScrollUp() && mCircleViews[DIRECTION_BOTTOM].getVisibility() == VISIBLE) {
                    mCircleViews[DIRECTION_BOTTOM].setVisibility(GONE);
                }
                if (!canChildScrollDown() && mCircleViews[DIRECTION_TOP].getVisibility() == VISIBLE) {
                    mCircleViews[DIRECTION_TOP].setVisibility(GONE);
                }

                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged && !canChildScrollUp()) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    mProgress[DIRECTION_TOP].setAlpha(STARTING_PROGRESS_ALPHA);
                } else if (yDiff < -mTouchSlop && !mIsBeingDragged && !canChildScrollDown()) {
                    mInitialMotionY = mInitialDownY - mTouchSlop;
                    mIsBeingDragged = true;
                    mProgress[DIRECTION_BOTTOM].setAlpha(STARTING_PROGRESS_ALPHA);
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                if (mIsBeingDragged) {
                    if (overscrollTop > 0 && !canChildScrollUp()) {
                        moveSpinner(DIRECTION_TOP, overscrollTop);
                    } else if (overscrollTop < 0 && !canChildScrollDown()) {
                        moveSpinner(DIRECTION_BOTTOM, -overscrollTop);
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overscrollTop > 0 && !canChildScrollUp()) {
                    finishSpinner(DIRECTION_TOP, overscrollTop);
                } else if (overscrollTop < 0 && !canChildScrollDown()) {
                    finishSpinner(DIRECTION_BOTTOM, -overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    /** <br> state. */

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && !mRefreshing) {
            // scale and show
            mRefreshing = true;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            setTargetOffsetTopAndBottom(DIRECTION_TOP, endTarget - mCurrentTargetOffsetTop,
                    true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(DIRECTION_TOP, mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    public void setLoading(boolean loading) {
        if (loading && !mLoading) {
            // scale and show
            mLoading = true;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            setTargetOffsetTopAndBottom(DIRECTION_BOTTOM, endTarget - mCurrentTargetOffsetTop,
                    true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(DIRECTION_BOTTOM, mLoadListener);
        } else {
            setLoading(loading, false /* notify */);
        }
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     *         progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    public boolean isLoading() {
        return mLoading;
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(DIRECTION_TOP, mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                startScaleDownAnimation(DIRECTION_TOP, mRefreshListener);
            }
        }
    }

    private void setLoading(boolean loading, final boolean notify) {
        if (mLoading != loading) {
            mNotify = notify;
            ensureTarget();
            mLoading = loading;
            if (mLoading) {
                animateOffsetToCorrectPosition(DIRECTION_BOTTOM, mCurrentTargetOffsetTop, mLoadListener);
            } else {
                startScaleDownAnimation(DIRECTION_BOTTOM, mLoadListener);
            }
        }
    }

    private void reset(int dir) {
        mCircleViews[dir].clearAnimation();
        mProgress[dir].stop();
        mCircleViews[dir].setVisibility(View.GONE);
        setColorViewAlpha(dir, MAX_ALPHA);
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(dir, 0 /* animation complete and view is hidden */);
        } else {
            setTargetOffsetTopAndBottom(dir, mOriginalOffsetTop - mCurrentTargetOffsetTop,
                    true /* requires update */);
        }
        if (dir == DIRECTION_TOP) {
            mCurrentTargetOffsetTop = mCircleViews[dir].getTop();
        } else {
            mCurrentTargetOffsetTop = getMeasuredHeight() - mCircleViews[dir].getTop() - mCircleHeight;
        }
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     *         scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mTarget, -1);
    }

    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mTarget, 1);
    }

    /** <br> tools. */

    /**
     * The refresh indicator starting and resting position is always positioned
     * near the top of the refreshing content. This position is a consistent
     * location, but can be adjusted in either direction based on whether or not
     * there is a toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than
     *            where the progress spinner is set to appear.
     * @param start The offset in pixels from the top of this view at which the
     *            progress spinner should appear.
     * @param end The offset in pixels from the top of this view at which the
     *            progress spinner should come to rest after a successful swipe
     *            gesture.
     */
    public void setProgressViewOffset(int dir, boolean scale, int start, int end) {
        mScale = scale;
        mCircleViews[dir].setVisibility(View.GONE);
        mOriginalOffsetTop = mCurrentTargetOffsetTop = start;
        mSpinnerFinalOffset = end;
        mUsingCustomStart = true;
        mCircleViews[dir].invalidate();
    }

    /**
     * The refresh indicator resting position is always positioned near the top
     * of the refreshing content. This position is a consistent location, but
     * can be adjusted in either direction based on whether or not there is a
     * toolbar or actionbar present.
     *
     * @param scale Set to true if there is no view at a higher z-order than
     *            where the progress spinner is set to appear.
     * @param end The offset in pixels from the top of this view at which the
     *            progress spinner should come to rest after a successful swipe
     *            gesture.
     */
    public void setProgressViewEndTarget(int dir, boolean scale, int end) {
        mSpinnerFinalOffset = end;
        mScale = scale;
        mCircleViews[dir].invalidate();
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    public void setSize(int dir, int size) {
        if (size != MaterialProgressDrawable.LARGE && size != MaterialProgressDrawable.DEFAULT) {
            return;
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (size == MaterialProgressDrawable.LARGE) {
            mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
        } else {
            mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleViews[dir].setImageDrawable(null);
        mProgress[dir].updateSizes(size);
        mCircleViews[dir].setImageDrawable(mProgress[dir]);
    }

    protected int getChildDrawingOrder(int dir, int childCount, int i) {
        if (mCircleViewIndexes[dir] < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mCircleViewIndexes[dir];
        } else if (i >= mCircleViewIndexes[dir]) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
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

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    /**
     * Get the diameter of the progress circle that is displayed as part of the
     * swipe to refresh layout. This is not valid until a measure pass has
     * completed.
     *
     * @return Diameter in pixels of the progress circle view.
     */
    public int getProgressCircleDiameter() {
        return mCircleViews != null && mCircleViews[0] != null ? mCircleViews[0].getMeasuredHeight() : 0;
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
        mCircleViews[dir].startAnimation(dir == DIRECTION_TOP ?
                mAnimateToTopCorrectPosition : mAnimateToBottomCorrectPosition);
    }

    private final Animation mAnimateToTopCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCircleViews[DIRECTION_TOP].getTop();
            setTargetOffsetTopAndBottom(DIRECTION_TOP, offset, false /* requires update */);
            mProgress[DIRECTION_TOP].setArrowScale(1 - interpolatedTime);
        }
    };

    private final Animation mAnimateToBottomCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }

            int realEndTarget = getMeasuredHeight() - endTarget - mCircleHeight;
            int realFrom = getMeasuredHeight() - mFrom - mCircleHeight;

            targetTop = (realFrom + (int) ((realEndTarget - realFrom) * interpolatedTime));
            int offset = targetTop - mCircleViews[DIRECTION_BOTTOM].getTop();
            setTargetOffsetTopAndBottom(DIRECTION_BOTTOM, -offset, false /* requires update */);
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
            mCircleViews[dir].startAnimation(dir == DIRECTION_TOP ?
                    mAnimateToTopStartPosition : mAnimateToBottomStartPosition);
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
        if (isAlphaUsedForScale()) {
            mStartingScale = mProgress[DIRECTION_TOP].getAlpha();
        } else {
            mStartingScale = ViewCompat.getScaleX(mCircleViews[DIRECTION_TOP]);
        }
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
        if (isAlphaUsedForScale()) {
            mStartingScale = mProgress[DIRECTION_BOTTOM].getAlpha();
        } else {
            mStartingScale = ViewCompat.getScaleX(mCircleViews[DIRECTION_BOTTOM]);
        }
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

    /**
     * Pre API 11, alpha is used to make the progress circle appear instead of scale.
     */
    private boolean isAlphaUsedForScale() {
        return android.os.Build.VERSION.SDK_INT < 11;
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

    /**
     * Pre API 11, this does an alpha animation.
     * @param progress
     */
    private void setAnimationProgress(int dir, float progress) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha(dir, (int) (progress * MAX_ALPHA));
        } else {
            ViewCompat.setScaleX(mCircleViews[dir], progress);
            ViewCompat.setScaleY(mCircleViews[dir], progress);
        }
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
        // Pre API 11, alpha is used in place of scale. Don't also use it to
        // show the trigger point.
        if (mScale && isAlphaUsedForScale()) {
            return null;
        }
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress[dir]
                        .setAlpha((int) (startingAlpha+ ((endingAlpha - startingAlpha)
                                * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mCircleViews[dir].setAnimationListener(null);
        mCircleViews[dir].clearAnimation();
        mCircleViews[dir].startAnimation(alpha);
        return alpha;
    }

    /** <br> color. */

    private void setColorViewAlpha(int dir, int targetAlpha) {
        mCircleViews[dir].getBackground().setAlpha(targetAlpha);
        mProgress[dir].setAlpha(targetAlpha);
    }

    /**
     * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
     */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        setProgressBackgroundColorSchemeResource(colorRes);
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color
     */
    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        for (int i = 0; i < 2; i ++) {
            mCircleViews[i].setBackgroundColor(color);
            mProgress[i].setBackgroundColor(color);
        }
    }

    /**
     * @deprecated Use {@link #setColorSchemeResources(int...)}
     */
    @Deprecated
    public void setColorScheme(@ColorInt int... colors) {
        setColorSchemeResources(colors);
    }

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = ContextCompat.getColor(getContext(), colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    @ColorInt
    public void setColorSchemeColors(int... colors) {
        ensureTarget();
        for (int i = 0; i < 2; i ++) {
            mProgress[i].setColorSchemeColors(colors);
        }
    }

    /** <br> nested scroll */

    // NestedScrollingChild

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
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
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

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !mRefreshing && !mLoading
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            for (int i = 0; i < 2; i ++) {
                moveSpinner(i, mTotalUnconsumed);
            }
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            for (int i = 0; i < 2; i ++) {
                mCircleViews[i].setVisibility(View.GONE);
            }
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
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            for (int i = 0; i < 2; i ++) {
                finishSpinner(i, mTotalUnconsumed);
            }
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
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
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(DIRECTION_TOP, mTotalUnconsumed);
            moveSpinner(DIRECTION_BOTTOM, mTotalUnconsumed);
        }
    }

    /** <br> listener. */

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        private int dir = DIRECTION_TOP;

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
                mProgress[dir].setAlpha(MAX_ALPHA);
                mProgress[dir].start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mCurrentTargetOffsetTop = mCircleViews[dir].getTop();
            } else {
                reset(dir);
            }
        }
    };

    private Animation.AnimationListener mLoadListener = new Animation.AnimationListener() {
        private int dir = DIRECTION_BOTTOM;

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
                mProgress[dir].setAlpha(MAX_ALPHA);
                mProgress[dir].start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onLoad();
                    }
                }
                mCurrentTargetOffsetTop = getMeasuredHeight() - mCircleViews[dir].getTop() - mCircleHeight;
            } else {
                reset(dir);
            }
        }
    };

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshAndLoadListener {
        void onRefresh();
        void onLoad();
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshAndLoadListener(OnRefreshAndLoadListener listener) {
        mListener = listener;
    }
}
