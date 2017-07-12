package com.wangdaye.mysplash.common.ui.widget.coverView;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Cover container layout.
 *
 * This view is a container for the cover of a collection.
 *
 * */

public class CoverContainerLayout extends FrameLayout {

    public CoverContainerLayout(@NonNull Context context) {
        super(context);
    }

    public CoverContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View cover = null;
        int height = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            View v = getChildAt(i);
            if (v instanceof ImageView) {
                cover = v;
            } else if (v.getMeasuredHeight() > height) {
                height = v.getMeasuredHeight();
            }
        }
        if (cover != null) {
            cover.measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
