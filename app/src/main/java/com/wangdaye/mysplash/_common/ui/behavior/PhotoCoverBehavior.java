package com.wangdaye.mysplash._common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

import com.wangdaye.mysplash._common.ui.widget.freedomSizeView.FreedomImageView;

/**
 * Cover behavior.
 * */

public class PhotoCoverBehavior<V extends FreedomImageView> extends CoordinatorLayout.Behavior<V> {

    /** <br> life cycle. */

    public PhotoCoverBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** <br> layout. */

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        int deltaWidth = child.getMeasuredWidth() - parent.getMeasuredWidth();
        child.layout(
                (int) (-deltaWidth / 2.0),
                0,
                (int) (parent.getMeasuredWidth() + deltaWidth / 2.0),
                child.getMeasuredHeight());
        return true;
    }
}
