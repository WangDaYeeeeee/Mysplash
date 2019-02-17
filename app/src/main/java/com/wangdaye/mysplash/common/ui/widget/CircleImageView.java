package com.wangdaye.mysplash.common.ui.widget;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

/**
 * Circle image view.
 *
 * A {@link ImageView} that has a circular bound.
 *
 * */

public class CircleImageView extends AppCompatImageView {

    public CircleImageView(Context context) {
        super(context);
        this.initialize();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
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
                            view.getHeight() - view.getPaddingBottom());
                }
            });
            setClipToOutline(true);
        }
    }
}
