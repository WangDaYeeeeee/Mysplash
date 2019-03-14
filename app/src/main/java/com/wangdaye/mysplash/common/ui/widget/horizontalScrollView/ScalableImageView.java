package com.wangdaye.mysplash.common.ui.widget.horizontalScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class ScalableImageView extends AppCompatImageView
        implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector;
    private OverScroller overScroller;

    private Matrix imageMatrix;
    private float[] matrixValues;

    private float dX;
    private float translateX;
    private float maxTranslateX;
    private float minTranslateX;
    private float initTranslateX;

    private float viewWidth;
    private float viewHeight;

    private static final int OVER_FLING = 20;

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageMatrix = new Matrix();
        matrixValues = new float[9];

        int[] size = DisplayUtils.getScreenSize(context);
        viewWidth = size[0];
        viewHeight = size[1];

        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, this);
        overScroller = new OverScroller(context);

        setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) viewWidth, (int) viewHeight);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setScaleType(ScaleType.CENTER_CROP);
        super.setImageDrawable(drawable);
    }

    @Override
    public void computeScroll() {
        if (overScroller.computeScrollOffset()) {
            setTranslation(overScroller.getCurrX() - initTranslateX, true);
            postInvalidate();
        }
    }

    public void getImageState() {
        setScaleType(ScaleType.MATRIX);

        imageMatrix.set(getImageMatrix());
        imageMatrix.getValues(matrixValues);

        dX = 0;
        translateX = matrixValues[Matrix.MTRANS_X];
        maxTranslateX = 0;
        float drawableWidth = getDrawableWidth(getDrawable());
        if (drawableWidth > 0) {
            minTranslateX = viewWidth - drawableWidth * matrixValues[Matrix.MSCALE_X];
        } else {
            minTranslateX = 0;
        }
        initTranslateX = translateX;
    }

    public void cancelFling() {
        overScroller.forceFinished(true);
    }

    /**
     * @return consumed dx.
     * */
    public float setTranslation(float dX, boolean fling) {
        float oldTranslateX = translateX;

        translateX = initTranslateX + dX;
        if (!fling) {
            translateX = Math.max(minTranslateX, translateX);
            translateX = Math.min(maxTranslateX, translateX);
        }

        imageMatrix.set(getImageMatrix());
        imageMatrix.postTranslate(translateX - oldTranslateX, 0);
        setImageMatrix(imageMatrix);

        return translateX - initTranslateX;
    }

    public void fling(float dX, float velocityX) {
        translateX = initTranslateX + dX;
        translateX = Math.max(minTranslateX, translateX);
        translateX = Math.min(maxTranslateX, translateX);

        overScroller.fling(
                (int) translateX, 0,
                (int) velocityX, 0,
                (int) minTranslateX, (int) maxTranslateX, 0, 0,
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

    // interface.

    // on touch listener.

    @SuppressLint("Recycle")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        MotionEvent obtain = MotionEvent.obtain(event);
        obtain.setLocation(event.getRawX(), event.getRawY());
        return gestureDetector.onTouchEvent(event);
    }

    // on gesture listener.

    @Override
    public boolean onDown(MotionEvent e) {
        cancelFling();
        getImageState();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // do nothing.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent down, MotionEvent current, float distanceX, float distanceY) {
        dX += distanceX;
        setTranslation(dX, false);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // do nothing.
    }

    @Override
    public boolean onFling(MotionEvent down, MotionEvent current, float velocityX, float velocityY) {
        fling(dX, velocityX);
        return false;
    }
}
