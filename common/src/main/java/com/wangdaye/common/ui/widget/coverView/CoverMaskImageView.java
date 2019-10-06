package com.wangdaye.common.ui.widget.coverView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.ColorUtils;

import com.wangdaye.common.R;

/**
 * Cover mask image view.
 *
 * This view is used to show a mask with a gradient effect for a cover image.
 *
 * */

public class CoverMaskImageView extends AppCompatImageView {

    private Paint paint;

    private float gradientAngle;
    private float fromAlpha;
    private float toAlpha;
    private int maskColor;

    public CoverMaskImageView(Context context) {
        this(context, null);
    }

    public CoverMaskImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverMaskImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        paint = new Paint();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoverMaskImageView, defStyleAttr, 0);
            setGradientAngle(a.getFloat(R.styleable.CoverMaskImageView_cml_gradient_angle, 90));
            fromAlpha = computeRealAlpha(a.getFloat(R.styleable.CoverMaskImageView_cml_from_alpha, 1));
            toAlpha = computeRealAlpha(a.getFloat(R.styleable.CoverMaskImageView_cml_to_alpha, 0));
            maskColor = a.getColor(R.styleable.CoverMaskImageView_cml_mask_color, Color.BLACK);
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

    private float computeRealAlpha(float alpha) {
        if (alpha < 0) {
            return 0;
        } else if (alpha > 1) {
            return 1;
        } else {
            return alpha;
        }
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
        paint.setShader(
                new LinearGradient(
                    (float) (cX + deltaX), (float) (cY - deltaY),
                    (float) (cX - deltaX), (float) (cY + deltaY),
                    new int[]{
                            ColorUtils.setAlphaComponent(maskColor, (int) (255 * toAlpha)),
                            ColorUtils.setAlphaComponent(maskColor, (int) (255 * fromAlpha))
                    },
                    null,
                    Shader.TileMode.CLAMP
                )
        );
    }
}
