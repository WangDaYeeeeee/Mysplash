package com.wangdaye.photo.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class PhotoRootCoordinatorLayout extends CoordinatorLayout {

    public PhotoRootCoordinatorLayout(@NonNull Context context) {
        super(context);
    }

    public PhotoRootCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoRootCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        View v;
        for (int i = 0, count = getChildCount(); i < count; i ++) {
            v = getChildAt(i);
            if (v instanceof LinearLayout) {
                v.setPadding(insets.left, 0, insets.right, 0);
            }
        }
        return false;
    }
}
