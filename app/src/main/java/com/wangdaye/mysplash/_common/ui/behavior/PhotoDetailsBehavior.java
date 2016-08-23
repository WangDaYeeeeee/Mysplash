package com.wangdaye.mysplash._common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.wangdaye.mysplash._common.ui.widget.FreedomImageView;

/**
 * Photo details behavior.
 * */

public class PhotoDetailsBehavior<V extends NestedScrollView> extends CoordinatorLayout.Behavior<V> {

    /** <br> life cycle. */

    public PhotoDetailsBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        for (int i = 0; i < parent.getChildCount(); i ++) {
            if (parent.getChildAt(i) instanceof FreedomImageView) {
                child.setPadding(0, parent.getChildAt(i).getMeasuredHeight(), 0, 0);
                child.setClipToPadding(false);
            }
        }
        return false;
    }
}
