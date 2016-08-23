package com.wangdaye.mysplash._common.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.Photo;

/**
 * Freedom image view.
 * */

public class FreedomImageView extends ImageView {
    // data
    private float width = 1;
    private float height = 0.666F;
    private boolean coverMode = false;

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
        this.coverMode = a.getBoolean(R.styleable.FreedomImageView_fiv_cover_mode, false);
        boolean existPhoto = a.getBoolean(R.styleable.FreedomImageView_fiv_exist_photo, false);
        a.recycle();

        if (existPhoto) {
            Photo p = Mysplash.getInstance().getPhoto();
            if (p != null) {
                width = p.width;
                height = p.height;
            }
        }
    }

    /** <br> UI. */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (coverMode) {
            float h = (float) (getResources().getDisplayMetrics().heightPixels * 0.6);
            float w = h / this.height * this.width;
            if (w < getResources().getDisplayMetrics().widthPixels) {
                int width = getResources().getDisplayMetrics().widthPixels;
                setMeasuredDimension(
                        width,
                        (int) (width / this.width * this.height));
            } else {
                setMeasuredDimension((int) w, (int) h);
            }
        } else {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(
                    width,
                    (int) (width / this.width * this.height));
        }
    }

    /** <br> data. */

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
