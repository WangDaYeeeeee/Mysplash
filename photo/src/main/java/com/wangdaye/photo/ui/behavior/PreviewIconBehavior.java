package com.wangdaye.photo.ui.behavior;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.annotations.NotNull;

/**
 * Preview icon behavior.
 *
 * Behavior class for icon container view in
 * {@link com.wangdaye.photo.activity.PreviewActivity}, it's used to help the
 * {@link CoordinatorLayout} to layout target child view.
 *
 * */

public class PreviewIconBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    public PreviewIconBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NotNull CoordinatorLayout parent, @NotNull V child, int layoutDirection) {
        child.layout(
                0,
                parent.getMeasuredHeight(),
                child.getMeasuredWidth(),
                parent.getMeasuredHeight() + child.getMeasuredHeight()
        );
        return true;
    }
}
