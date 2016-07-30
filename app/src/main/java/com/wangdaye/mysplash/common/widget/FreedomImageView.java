package com.wangdaye.mysplash.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Freedom image view.
 * */

public class FreedomImageView extends ImageView {
    // data
    private float width = 1;
    private float height = 0.666F;

    /** <br> life cycle. */

    public FreedomImageView(Context context) {
        super(context);
    }

    public FreedomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreedomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreedomImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** <br> UI. */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(
                width,
                (int) (width / this.width * this.height));
    }

    /** <br> data. */

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
