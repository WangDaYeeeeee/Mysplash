package com.wangdaye.mysplash.common.ui.widget.coordinatorView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Navigation bar view.
 *
 * This view can simulate the height of navigation bar. You can fill the navigation bar by this view.
 *
 * */

public class NavigationBarView extends View {

    public NavigationBarView(Context context) {
        super(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                DisplayUtils.getNavigationBarHeight(getResources())
        );
    }
}
