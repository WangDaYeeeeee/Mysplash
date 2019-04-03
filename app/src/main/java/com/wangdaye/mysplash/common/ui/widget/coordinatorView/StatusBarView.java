package com.wangdaye.mysplash.common.ui.widget.coordinatorView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.FloatRange;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

/**
 * Status bar view.
 *
 * This view can simulate the height of status bar. You can fill the status bar by this view.
 *
 * */

public class StatusBarView extends View {

    private ObjectAnimator alphaAnimator;

    // if set true, the status bar view will add a translucent black drawMask when the view is being drawn.
    private boolean drawMask = false;

    private boolean translucentMode = false; // set the view is translucent or not.
    private boolean fillInMode = false; // set the view is just used to fill in a blank area.

    private boolean initState = false;

    // these 2 values only effective in translucent mode. for example:
    // initAlpha < 0  --> initAlpha = LIGHT_INIT_MASK_ALPHA / DARK_INIT_MASK_ALPHA.
    // initAlpha >= 0 --> initAlpha = custom value.
    @FloatRange(to = 1.0)
    private float initAlpha = -1;
    @FloatRange(to = 1.0)
    private float darkerAlpha = -1;

    public static final float LIGHT_INIT_MASK_ALPHA = 0.03f;
    public static final float DARK_INIT_MASK_ALPHA = 0.2f;
    public static final float DARKER_MASK_ALPHA = 0.2f;

    public StatusBarView(Context context) {
        super(context);
        this.initialize(context, null, 0, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs, 0, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.StatusBarView, defStyleAttr, defStyleRes);
        this.translucentMode = a.getBoolean(R.styleable.StatusBarView_sbv_translucent_mode, false);
        this.fillInMode = a.getBoolean(R.styleable.StatusBarView_sbv_fill_in_mode, false);
        this.initAlpha = Math.min(1.0f, a.getFloat(R.styleable.StatusBarView_sbv_init_alpha, -1));
        this.darkerAlpha = Math.min(1.0f, a.getFloat(R.styleable.StatusBarView_sbv_darker_alpha, -1));
        a.recycle();

        if (translucentMode) {
            setBackgroundResource(android.R.color.black);
            setInitTranslucentAlpha();
        } else if (fillInMode) {
            setBackgroundColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                && ThemeManager.getInstance(c).isLightTheme()) {
            setDrawMask(true);
            setBackgroundColor(ThemeManager.getPrimaryColor(getContext()));
        } else {
            setDrawMask(false);
            setBackgroundColor(ThemeManager.getPrimaryDarkColor(getContext()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                DisplayUtils.getStatusBarHeight(getResources())
        );
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

    /**
     * Reset the alpha of StatusBarView to the initially state.
     * */
    public void setInitTranslucentAlpha() {
        if (translucentMode) {
            setInitState(true);
            cancelAnimator();
            setAlpha(getTargetAlpha());
        }
    }

    /**
     * Start alpha animation to change the alpha to initially state.
     * */
    public void animToInitAlpha() {
        if (translucentMode) {
            setInitState(true);
            changeAlpha(getTargetAlpha());
        }
    }

    /**
     * Start alpha animation to change the alpha to darker state.
     * */
    public void animToDarkerAlpha() {
        if (translucentMode) {
            setInitState(false);
            changeAlpha(getTargetAlpha());
        }
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

    /**
     * Get the alpha by build version code, theme and state.
     * */
    private float getTargetAlpha() {
        if (isInitState()) {
            if (initAlpha >= 0) {
                return initAlpha;
            } else if (ThemeManager.getInstance(getContext()).isLightTheme()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return LIGHT_INIT_MASK_ALPHA;
                } else {
                    return DARKER_MASK_ALPHA;
                }
            } else {
                return DARK_INIT_MASK_ALPHA;
            }
        } else {
            return darkerAlpha >= 0 ? darkerAlpha : DARKER_MASK_ALPHA;
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
