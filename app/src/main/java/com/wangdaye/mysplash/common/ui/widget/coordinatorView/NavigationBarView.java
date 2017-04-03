package com.wangdaye.mysplash.common.ui.widget.coordinatorView;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    /** <br> life cycle. */

    public NavigationBarView(Context context) {
        super(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** <br> UI. */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                DisplayUtils.getNavigationBarHeight(getResources()));
    }
}
