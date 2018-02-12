package com.wangdaye.mysplash.common.ui.widget.fullScreenView;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Full screen image view.
 *
 * This imageView will size as the same as screen.
 *
 * */

public class FullScreenImageView extends AppCompatImageView {

    public FullScreenImageView(Context context) {
        super(context);
    }

    public FullScreenImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] size = DisplayUtils.getScreenSize(getContext());
        setMeasuredDimension(size[0], size[1]);
    }
}
