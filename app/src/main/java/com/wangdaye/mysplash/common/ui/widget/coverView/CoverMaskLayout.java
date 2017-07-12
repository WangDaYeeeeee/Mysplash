package com.wangdaye.mysplash.common.ui.widget.coverView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Cover mask layout.
 *
 * This layout is used to show a mask with a gradient effect for a cover image.
 *
 * */

public class CoverMaskLayout extends RelativeLayout {

    private Paint paint;
    private float gradientAngle;

    public CoverMaskLayout(Context context) {
        this(context, null);
    }

    public CoverMaskLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverMaskLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        paint = new Paint();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoverMaskLayout, defStyleAttr, 0);
            setGradientAngle(a.getFloat(R.styleable.CoverMaskLayout_cml_gradient_angle, 90));
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPaintStyle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
    }

    public void setGradientAngle(float angle) {
        gradientAngle = (angle + 360) % 360;
        setPaintStyle();
        invalidate();
    }

    private void setPaintStyle() {
        double deltaX;
        double deltaY;
        if (gradientAngle == 90 || gradientAngle == 270) {
            deltaX = 0;
        } else {
            deltaX = 0.5 * getMeasuredHeight() / Math.tan(gradientAngle * Math.PI / 180.0);
            deltaX = Math.min(deltaX, getMeasuredWidth() * 0.5);
            deltaX = Math.max(deltaX, getMeasuredWidth() * -0.5);
        }
        if (gradientAngle == 90) {
            deltaY = 0.5 * getMeasuredHeight();
        } else if (gradientAngle == 270) {
            deltaY = -0.5 * getMeasuredHeight();
        } else {
            deltaY = 0.5 * getMeasuredWidth() * Math.tan(gradientAngle * Math.PI / 180.0);
            deltaY = Math.min(deltaY, getMeasuredHeight() * 0.5);
            deltaY = Math.max(deltaY, getMeasuredHeight() * -0.5);
        }

        double cX = getMeasuredWidth() * 0.5;
        double cY = getMeasuredHeight() * 0.5;
        if (ThemeManager.getInstance(getContext()).isLightTheme()) {
            paint.setShader(new LinearGradient(
                    (float) (cX + deltaX), (float) (cY - deltaY),
                    (float) (cX - deltaX), (float) (cY + deltaY),
                    new int[]{
                            Color.argb((int) (255 * 0.70), 250, 250, 250),
                            Color.argb((int) (255 * 0.815), 250, 250, 250),
                            Color.argb((int) (255 * 0.91), 250, 250, 250),
                            Color.argb((int) (255 * 0.965), 250, 250, 250),
                            Color.argb(255, 250, 250, 250)},
                    null,
                    Shader.TileMode.CLAMP));
        } else {
            paint.setShader(new LinearGradient(
                    (float) (cX + deltaX), (float) (cY + deltaY),
                    (float) (cX - deltaX), (float) (cY - deltaY),
                    new int[]{
                            Color.argb((int) (255 * 0.78), 33, 33, 33),
                            Color.argb((int) (255 * 0.875), 33, 33, 33),
                            Color.argb((int) (255 * 0.95), 33, 33, 33),
                            Color.argb((int) (255 * 0.985), 33, 33, 33),
                            Color.argb(255, 33, 33, 33)},
                    null,
                    Shader.TileMode.CLAMP));
        }
    }
}
