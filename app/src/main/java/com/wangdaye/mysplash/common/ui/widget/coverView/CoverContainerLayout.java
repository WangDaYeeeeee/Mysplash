package com.wangdaye.mysplash.common.ui.widget.coverView;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Cover container layout.
 *
 * This view is a container for the cover of a collection.
 *
 * */

public class CoverContainerLayout extends ViewGroup {

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
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int maxWidth = widthMode == MeasureSpec.UNSPECIFIED
                ? Integer.MAX_VALUE : MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = heightMode == MeasureSpec.UNSPECIFIED
                ? Integer.MAX_VALUE : MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        View cover = null;

        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            if (child instanceof ImageView) {
                cover = child;
                continue;
                // measure cover image later.
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            measureChildWithMargins(
                    child,
                    MeasureSpec.makeMeasureSpec(
                            maxWidth - lp.leftMargin - lp.rightMargin,
                            widthMode
                    ), 0,
                    MeasureSpec.makeMeasureSpec(
                            maxHeight - height - lp.topMargin - lp.bottomMargin,
                            MeasureSpec.UNSPECIFIED
                    ), height
            );

            width = Math.max(width, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
            height += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }

        if (cover != null) {
            cover.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            );
        }

        setMeasuredDimension(
                Math.min(width, maxWidth),
                Math.min(height, maxHeight)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = 0;

        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);
            if (child == null || child.getVisibility() == GONE) {
                continue;
            }

            if (child instanceof ImageView) {
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int left = (getMeasuredWidth() - child.getMeasuredWidth()) / 2;
            int top = height + lp.topMargin;
            child.layout(
                    left,
                    top,
                    left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight()
            );

            height += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new MarginLayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
