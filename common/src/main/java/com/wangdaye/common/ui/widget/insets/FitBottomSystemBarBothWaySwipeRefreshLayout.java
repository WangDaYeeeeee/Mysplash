package com.wangdaye.common.ui.widget.insets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.wangdaye.common.R;
import com.wangdaye.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;

public class FitBottomSystemBarBothWaySwipeRefreshLayout extends BothWaySwipeRefreshLayout {

    public FitBottomSystemBarBothWaySwipeRefreshLayout(Context context) {
        super(context);
        setFitsSystemWindows(true);
    }

    public FitBottomSystemBarBothWaySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFitsSystemWindows(true);
    }

    @Override
    public boolean fitSystemWindows(Rect insets) {
        setDragTriggerDistance(
                DIRECTION_BOTTOM,
                getResources().getDimensionPixelSize(R.dimen.normal_margin) + insets.bottom
        );
        return false;
    }
}
