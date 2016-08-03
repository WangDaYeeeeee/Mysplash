package com.wangdaye.mysplash.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wangdaye.mysplash.R;

/**
 * Freedom image view.
 * */

public class FreedomImageView extends ImageView {
    // data
    private float width = 1;
    private float height = 0.666F;
    private boolean fullScreenWidth = true;

    /** <br> life cycle. */

    public FreedomImageView(Context context) {
        super(context);
    }

    public FreedomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs, 0, 0);
    }

    public FreedomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreedomImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FreedomImageView, defStyleAttr, defStyleRes);
        this.fullScreenWidth = a.getBoolean(R.styleable.FreedomImageView_fiv_full_screen_width, true);
        a.recycle();
    }

    /** <br> UI. */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fullScreenWidth) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(
                    width,
                    (int) (width / this.width * this.height));
        } else {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(
                    (int) (height / this.height * this.width),
                    height);
        }
    }

    /** <br> data. */

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getPhotoWidth() {
        return width;
    }

    public float getPhotoHeight() {
        return height;
    }
}
