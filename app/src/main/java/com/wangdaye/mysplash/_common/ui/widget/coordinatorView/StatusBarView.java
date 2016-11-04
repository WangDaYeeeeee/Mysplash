package com.wangdaye.mysplash._common.ui.widget.coordinatorView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Status bar view.
 * */

public class StatusBarView extends View {
    // data
    private boolean mask = false;

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
            canvas.drawColor(Color.argb((int) (255 * 0.2), 0, 0, 0));
        }
    }

    public void setMask(boolean b) {
        this.mask = b;
        invalidate();
    }
}
