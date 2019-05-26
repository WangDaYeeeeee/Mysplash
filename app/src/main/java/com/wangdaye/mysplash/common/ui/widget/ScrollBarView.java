package com.wangdaye.mysplash.common.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

public class ScrollBarView extends View {

    private Paint paint;

    @FloatRange(from = 0, to = 1) private float scrollBarAlpha;
    @FloatRange(from = 0, to = 1) private float backgroundAlpha;

    private int scrollBarWidth;
    @FloatRange(from = 0, to = 1) private float scrollBarIndex;

    @ColorInt private int scrollBarColor;
    @ColorInt private int backgroundColor;

    private ObjectAnimator showAnimator;
    private ObjectAnimator dismissAnimator;

    private static final float DEFAULT_SCROLLBAR_ALPHA = 0.5f;
    private static final float DEFAULT_BACKGROUND_ALPHA = 0.15f;

    public ScrollBarView(Context context) {
        super(context);
        this.initialize();
    }

    public ScrollBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public ScrollBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        scrollBarAlpha = DEFAULT_SCROLLBAR_ALPHA;
        backgroundAlpha = DEFAULT_BACKGROUND_ALPHA;

        scrollBarWidth = 0;
        scrollBarIndex = 0;

        scrollBarColor = Color.WHITE;
        backgroundColor = Color.BLACK;
        setAlpha(0);

        showAnimator = ObjectAnimator.ofFloat(
                this, "alpha", getAlpha(), 1
        ).setDuration(100);

        dismissAnimator = ObjectAnimator.ofFloat(
                this, "alpha", getAlpha(), 0
        ).setDuration(200);
        dismissAnimator.setStartDelay(600);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(backgroundColor);
        paint.setAlpha((int) (backgroundAlpha * 255));
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        paint.setColor(scrollBarColor);
        paint.setAlpha((int) (scrollBarAlpha * 255));
        int left = (int) ((getMeasuredWidth() - scrollBarWidth) * scrollBarIndex);
        canvas.drawRect(left, 0, left + scrollBarWidth, getMeasuredHeight(), paint);
    }

    @FloatRange(from = 0, to = 1)
    public float getScrollBarAlpha() {
        return scrollBarAlpha;
    }

    public void setScrollBarAlpha(@FloatRange(from = 0, to = 1) float scrollBarAlpha) {
        this.scrollBarAlpha = scrollBarAlpha;
        invalidate();
    }

    @FloatRange(from = 0, to = 1)
    public float getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(@FloatRange(from = 0, to = 1) float backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        invalidate();
    }

    public int getScrollBarWidth() {
        return scrollBarWidth;
    }

    public void setScrollBarWidth(int scrollBarWidth) {
        this.scrollBarWidth = scrollBarWidth;
        invalidate();
    }

    @FloatRange(from = 0, to = 1)
    public float getScrollBarIndex() {
        return scrollBarIndex;
    }

    public void setScrollBarIndex(@FloatRange(from = 0, to = 1) float scrollBarIndex) {
        this.scrollBarIndex = scrollBarIndex;
        invalidate();
    }

    @ColorInt
    public int getScrollBarColor() {
        return scrollBarColor;
    }

    public void setScrollBarColor(@ColorInt int scrollBarColor) {
        this.scrollBarColor = scrollBarColor;
        invalidate();
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public void setDisplayState(boolean show) {
        dismissAnimator.cancel();

        if (show) {
            showAnimator.cancel();
            showAnimator.setFloatValues(getAlpha(), 1);
            showAnimator.start();
        } else {
            dismissAnimator.setFloatValues(getAlpha(), 0);
            dismissAnimator.start();
        }
    }
}
