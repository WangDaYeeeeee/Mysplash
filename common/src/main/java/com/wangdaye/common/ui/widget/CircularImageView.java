package com.wangdaye.common.ui.widget;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Circle image view.
 *
 * A {@link ImageView} that has a circular bound.
 *
 * */

public class CircularImageView extends AppCompatImageView {

    public CircularImageView(Context context) {
        super(context);
        this.initialize();
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(
                            view.getPaddingLeft(),
                            view.getPaddingTop(),
                            view.getWidth() - view.getPaddingRight(),
                            view.getHeight() - view.getPaddingBottom()
                    );
                }
            });
            setClipToOutline(true);
        }
    }
}
