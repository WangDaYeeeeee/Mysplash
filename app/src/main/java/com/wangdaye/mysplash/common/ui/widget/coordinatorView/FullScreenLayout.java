package com.wangdaye.mysplash.common.ui.widget.coordinatorView;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Full screen layout.
 *
 * A full screen size layout.
 * */

public class FullScreenLayout extends RelativeLayout {

    public FullScreenLayout(@NonNull Context context) {
        super(context);
    }

    public FullScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FullScreenLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < getResources().getDisplayMetrics().heightPixels) {
            super.onMeasure(
                    MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.EXACTLY));
            setMeasuredDimension(screenWidth, screenHeight);
        }
    }
}
