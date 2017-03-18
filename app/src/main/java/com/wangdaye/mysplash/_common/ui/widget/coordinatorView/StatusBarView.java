package com.wangdaye.mysplash._common.ui.widget.coordinatorView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Status bar view.
 * */

public class StatusBarView extends View {
    // widget
    private ObjectAnimator alphaAnimator;

    // data
    private boolean mask = false;

    private boolean initAlpha = false;
    public static final float LIGHT_INIT_MASK_ALPHA = 0.03f;
    public static final float DARK_INIT_MASK_ALPHA = 0.2f;
    public static final float DARKER_MASK_ALPHA = 0.2f;

    /** <br> life cycle. */

    public StatusBarView(Context context) {
        super(context);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** <br> UI. */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getResources().getDisplayMetrics().widthPixels,
                DisplayUtils.getStatusBarHeight(getResources()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mask) {
            canvas.drawColor(Color.argb((int) (255 * 0.1), 0, 0, 0));
        }
    }

    public void setMask(boolean b) {
        this.mask = b;
        invalidate();
    }

    public void setInitMaskAlpha() {
        setInitAlpha(true);
        cancelAnimator();
        setAlpha(getTargetAlpha());
    }

    public void animToInitAlpha() {
        setInitAlpha(true);
        changeAlpha(getTargetAlpha());
    }

    public void animToDarkerAlpha() {
        setInitAlpha(false);
        changeAlpha(getTargetAlpha());
    }

    private void changeAlpha(float alphaTo) {
        cancelAnimator();
        if (getAlpha() != alphaTo) {
            alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), alphaTo)
                    .setDuration(150);
            alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnimator.start();
        }
    }

    private void cancelAnimator() {
        if (alphaAnimator != null) {
            alphaAnimator.cancel();
        }
    }

    /** <br> data. */

    private float getTargetAlpha() {
        if (isInitAlpha()) {
            if (Mysplash.getInstance().isLightTheme()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return LIGHT_INIT_MASK_ALPHA;
                } else {
                    return DARKER_MASK_ALPHA;
                }
            } else {
                return DARK_INIT_MASK_ALPHA;
            }
        } else {
            return DARKER_MASK_ALPHA;
        }
    }

    public boolean isInitAlpha() {
        return initAlpha;
    }

    public void setInitAlpha(boolean initAlpha) {
        this.initAlpha = initAlpha;
    }
}
