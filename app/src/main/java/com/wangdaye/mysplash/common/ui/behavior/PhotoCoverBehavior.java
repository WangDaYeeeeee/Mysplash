package com.wangdaye.mysplash.common.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;

/**
 * Photo cover behavior.
 *
 * Behavior class for {@link FreedomImageView}, it's used to help {@link CoordinatorLayout} to
 * layout target child view.
 *
 * */

public class PhotoCoverBehavior<V extends FreedomImageView> extends CoordinatorLayout.Behavior<V> {

    public PhotoCoverBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

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
