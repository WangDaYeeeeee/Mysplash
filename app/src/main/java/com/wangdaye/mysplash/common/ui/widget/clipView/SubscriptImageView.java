package com.wangdaye.mysplash.common.ui.widget.clipView;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

/**
 * Subscript image view.
 *
 * This ImageView class is used to show a subscript for avatar.
 *
 * */


public class SubscriptImageView extends ImageView {

    /** <br> life cycle. */

    public SubscriptImageView(Context context) {
        super(context);
    }

    public SubscriptImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubscriptImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SubscriptImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** <br> UI. */

    public void setSubscript(@DrawableRes int id) {
        setImageResource(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    getDrawable().getOutline(outline);
                }
            });
            setClipToOutline(true);
        }
    }
}
