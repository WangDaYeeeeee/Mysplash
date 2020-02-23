package com.wangdaye.common.ui.widget.insets;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

public class FitBottomSystemBarViewPager extends ViewPager {

    private Rect windowInsets = new Rect();

    public FitBottomSystemBarViewPager(@NonNull Context context) {
        super(context);
    }

    public FitBottomSystemBarViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        super.setOnApplyWindowInsetsListener((v, insets) -> {
            Rect waterfull = Utils.getWaterfullInsets(insets);
            fitSystemWindows(
                    new Rect(
                            insets.getSystemWindowInsetLeft() + waterfull.left,
                            insets.getSystemWindowInsetTop() + waterfull.top,
                            insets.getSystemWindowInsetRight() + waterfull.right,
                            insets.getSystemWindowInsetBottom() + waterfull.bottom
                    )
            );
            return listener == null ? insets : listener.onApplyWindowInsets(v, insets);
        });
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        windowInsets = insets;
        return false;
    }

    public Rect getWindowInsets() {
        return windowInsets;
    }
}
