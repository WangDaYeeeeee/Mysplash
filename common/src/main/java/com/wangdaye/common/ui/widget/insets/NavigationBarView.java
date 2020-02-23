package com.wangdaye.common.ui.widget.insets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Navigation bar view.
 *
 * This view can simulate the height of navigation bar. You can fill the navigation bar by this view.
 *
 * */

public class NavigationBarView extends View {

    private int height;

    public NavigationBarView(Context context) {
        this(context, null);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.height = 0;

        setFitsSystemWindows(true);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        height = insets.bottom;
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }
}
