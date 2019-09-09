package com.wangdaye.common.ui.widget.windowInsets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.common.R;
import com.wangdaye.common.base.application.MysplashApplication;
import com.wangdaye.common.utils.manager.ThemeManager;

/**
 * Status bar view.
 *
 * This view can simulate the height of status bar. You can fill the status bar by this view.
 *
 * */

public class StatusBarView extends View {

    @Nullable private ApplyWindowInsetsLayout applyWindowInsetsLayout;
    @NonNull private Rect windowInsets;

    // if set true, the status bar view will add a translucent black drawMask when the view is being drawn.
    private boolean drawMask = false;

    private boolean translucentMode; // set the view is translucent or not.
    private boolean fillInMode; // set the view is just used to fill in a blank area.

    private boolean initState = false;

    // these 2 values only effective in translucent mode. for example:
    // initAlpha < 0  --> initAlpha = LIGHT_INIT_MASK_ALPHA / DARK_INIT_MASK_ALPHA.
    // initAlpha >= 0 --> initAlpha = custom value.
    @FloatRange(to = 1.0)
    private float initAlpha;
    @FloatRange(to = 1.0)
    private float darkerAlpha;

    public static final float LIGHT_INIT_MASK_ALPHA = 0.03f;
    public static final float DARK_INIT_MASK_ALPHA = 0.2f;
    public static final float DEFAULT_INIT_MASK_ALPHA = 0;
    public static final float DEFAULT_DARKER_MASK_ALPHA = 0.2f;

    public StatusBarView(Context context) {
        this(context, null);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyWindowInsetsLayout = null;
        windowInsets = new Rect(-1, -1, -1, -1);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView, defStyleAttr, 0);
        this.translucentMode = a.getBoolean(R.styleable.StatusBarView_sbv_translucent_mode, false);
        this.fillInMode = a.getBoolean(R.styleable.StatusBarView_sbv_fill_in_mode, false);
        this.initAlpha = Math.min(1.0f, a.getFloat(R.styleable.StatusBarView_sbv_init_alpha, -1));
        this.darkerAlpha = Math.min(1.0f, a.getFloat(R.styleable.StatusBarView_sbv_darker_alpha, -1));
        a.recycle();

        if (translucentMode) {
            setBackgroundResource(android.R.color.black);
            switchToInitAlpha();
        } else if (fillInMode) {
            setBackgroundColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                && ThemeManager.getInstance(context).isLightTheme()) {
            setDrawMask(true);
            setBackgroundColor(ThemeManager.getPrimaryColor(getContext()));
        } else {
            setDrawMask(false);
            setBackgroundColor(ThemeManager.getPrimaryColor(getContext()));
        }
    }

    private boolean ensureApplyWindowInsetsLayout() {
        if (applyWindowInsetsLayout != null) {
            return true;
        }
        View view = this;
        do {
            view = (View) view.getParent();
        } while (view != null && !(view instanceof ApplyWindowInsetsLayout));

        if (view != null) {
            applyWindowInsetsLayout = (ApplyWindowInsetsLayout) view;
            return true;
        }
        return false;
    }

    public void setWindowInsets(@NonNull Rect windowInsets) {
        this.windowInsets.set(windowInsets);
    }

    public boolean isValidWindowInsets() {
        return windowInsets.left != -1
                && windowInsets.top != -1
                && windowInsets.right != -1
                && windowInsets.bottom != -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isValidWindowInsets()) {
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    windowInsets.top
            );
        } else if (ensureApplyWindowInsetsLayout()) {
            assert applyWindowInsetsLayout != null;
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    applyWindowInsetsLayout.getWindowInsets().top
            );
        } else {
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    MysplashApplication.getInstance().getWindowInsets().top
            );
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawMask && !fillInMode) {
            canvas.drawColor(Color.argb((int) (255 * 0.1), 0, 0, 0));
        }
    }

    /**
     * Set draw drawMask or not.
     *
     * @param draw draw drawMask or not.
     * */
    public void setDrawMask(boolean draw) {
        this.drawMask = draw;
        invalidate();
    }

    public void switchToInitAlpha() {
        if (translucentMode) {
            setInitState(true);
            setAlpha(getTargetAlpha());
        }
    }

    public void switchToDarkerAlpha() {
        if (translucentMode) {
            setInitState(false);
            setAlpha(getTargetAlpha());
        }
    }

    /**
     * Get the alpha by build version code, theme and state.
     * */
    private float getTargetAlpha() {
        if (isInitState()) {
            if (initAlpha >= 0) {
                return initAlpha;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return DEFAULT_INIT_MASK_ALPHA;
            } else {
                return DEFAULT_DARKER_MASK_ALPHA;
            }
        } else {
            return darkerAlpha >= 0 ? darkerAlpha : DEFAULT_DARKER_MASK_ALPHA;
        }
    }

    public boolean isInitState() {
        return initState;
    }

    public void setInitState(boolean initState) {
        this.initState = initState;
    }

    public void setDarkerAlpha(float alpha) {
        darkerAlpha = alpha;
    }
}
