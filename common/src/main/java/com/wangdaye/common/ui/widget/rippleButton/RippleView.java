package com.wangdaye.common.ui.widget.rippleButton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.annotation.ColorInt;

/**
 * Ripple view.
 *
 * This view can draw reveal animation for {@link RippleButton}.
 *
 * */

public class RippleView extends View {

    private Paint paint;

    private RippleAnim anim;

    private int centerX;
    private int centerY;
    private int radius;

    private class RippleAnim extends Animation {

        private int startX;
        private int startY;
        private int endRadius;

        RippleAnim(int x, int y) {
            this.startX = x;
            this.startY = y;
            calcEndRadius(x, y);
            setDuration(200);
            setInterpolator(new AccelerateDecelerateInterpolator());
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            setDrawData(
                    startX,
                    startY,
                    (int) (endRadius * interpolatedTime)
            );
            invalidate();
        }

        // data.

        private void calcEndRadius(int x, int y) {
            int[] radius = new int[] {
                    (int) Math.pow(Math.pow(x, 2) + Math.pow(y, 2), 0.5) + 1,
                    (int) Math.pow(Math.pow(getMeasuredWidth() - x, 2) + Math.pow(y, 2), 0.5) + 1,
                    (int) Math.pow(Math.pow(x, 2) + Math.pow(getMeasuredHeight() - y, 2), 0.5) + 1,
                    (int) Math.pow(Math.pow(getMeasuredWidth() - x, 2) + Math.pow(getMeasuredHeight() - y, 2), 0.5) + 1
            };
            endRadius = radius[0];
            for (int i = 1; i < radius.length; i ++) {
                if (radius[i] > endRadius) {
                    endRadius = radius[i];
                }
            }
        }
    }

    public RippleView(Context context) {
        super(context);
        this.initialize();
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        setDrawData(0, 0, 0);

        this.paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
    }

    // draw.

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    // control.

    public void drawRipple(int startX, int startY) {
        if (anim != null) {
            anim.cancel();
        }
        anim = new RippleAnim(startX, startY);
        startAnimation(anim);
    }

    private void setDrawData(int x, int y, int r) {
        this.centerX = x;
        this.centerY = y;
        this.radius = r;
    }

    public void setColor(@ColorInt int color) {
        this.paint.setColor(color);
    }
}
