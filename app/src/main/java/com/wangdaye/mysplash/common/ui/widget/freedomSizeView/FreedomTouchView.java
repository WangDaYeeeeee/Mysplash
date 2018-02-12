package com.wangdaye.mysplash.common.ui.widget.freedomSizeView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Freedom touch view.
 *
 * A translucent view, it is used to simulation {@link FreedomImageView} to measure size.
 *
 * */

public class FreedomTouchView extends View {

    private Paint paint;

    private float width = 1;
    private float height = 0.666F;

    private boolean coverMode = false;
    private boolean showShadow;

    public FreedomTouchView(Context context) {
        super(context);
        this.initialize(context, null, 0, 0);
    }

    public FreedomTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs, 0, 0);
    }

    public FreedomTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(context, attrs, defStyleAttr, 0);
    }

    private void initialize(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.paint = new Paint();

        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FreedomTouchView, defStyleAttr, defStyleRes);
        this.coverMode = a.getBoolean(R.styleable.FreedomTouchView_ftv_cover_mode, false);
        a.recycle();

        this.showShadow = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] sizes = FreedomImageView.getMeasureSize(
                getContext(), MeasureSpec.getSize(widthMeasureSpec), width, height, coverMode);
        setMeasuredDimension(sizes[0], sizes[1]);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showShadow) {
            int bottomTextHeight = (int) new DisplayUtils(getContext()).dpToPx(72);
            paint.setShader(new LinearGradient(
                    0, getMeasuredHeight(),
                    0, getMeasuredHeight() - bottomTextHeight,
                    new int[]{
                            Color.argb((int) (255 * 0.3), 0, 0, 0),
                            Color.argb((int) (255 * 0.1), 0, 0, 0),
                            Color.argb((int) (255 * 0.03), 0, 0, 0),
                            Color.argb(0, 0, 0, 0)},
                    null,
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        }
    }

    public float[] getSize() {
        return new float[] {width, height};
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
        if (getMeasuredWidth() != 0) {
            requestLayout();
        }
    }

    public void setShowShadow(boolean show) {
        this.showShadow = show;
    }
}
