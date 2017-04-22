package com.wangdaye.mysplash.common.ui.widget.freedomSizeView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.R;

/**
 * Freedom touch view.
 *
 * A translucent view, it is used to simulation {@link FreedomImageView} to measure size.
 *
 * */

public class FreedomTouchView extends View {

    private float width = 1;
    private float height = 0.666F;

    public FreedomTouchView(Context context) {
        super(context);
    }

    public FreedomTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreedomTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] size = getMeasureSize(MeasureSpec.getSize(widthMeasureSpec));
        setMeasuredDimension(size[0], size[1]);
    }

    public float[] getSize() {
        return new float[] {width, height};
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
        if (getMeasuredWidth() != 0) {
            /*
            int[] size = getMeasureSize(getMeasuredWidth());

            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = size[0];
            params.height = size[1];
            setLayoutParams(params);
            */
            requestLayout();
        }
    }

    private int[] getMeasureSize(int measureWidth) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float limitHeight = screenHeight
                - getResources().getDimensionPixelSize(R.dimen.photo_info_base_view_height);

        if (1.0 * height / width * screenWidth <= limitHeight) {
            return new int[] {
                    screenWidth,
                    (int) limitHeight};
        } else {
            return new int[] {
                    measureWidth,
                    (int) (measureWidth * height / width)};
        }
    }
}
