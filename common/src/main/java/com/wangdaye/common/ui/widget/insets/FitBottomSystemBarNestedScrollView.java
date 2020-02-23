package com.wangdaye.common.ui.widget.insets;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

public class FitBottomSystemBarNestedScrollView extends NestedScrollView {

    private Rect windowInsets = new Rect();

    public FitBottomSystemBarNestedScrollView(@NonNull Context context) {
        super(context);
        ViewCompat.setOnApplyWindowInsetsListener(this, null);
    }

    public FitBottomSystemBarNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ViewCompat.setOnApplyWindowInsetsListener(this, null);
    }

    public FitBottomSystemBarNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewCompat.setOnApplyWindowInsetsListener(this, null);
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
    public boolean fitSystemWindows(Rect insets) {
        windowInsets = insets;
        setPadding(windowInsets.left, 0, windowInsets.right, windowInsets.bottom);
        return false;
    }

    public Rect getWindowInsets() {
        return windowInsets;
    }
}