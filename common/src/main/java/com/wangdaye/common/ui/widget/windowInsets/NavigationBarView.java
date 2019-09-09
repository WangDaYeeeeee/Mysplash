package com.wangdaye.common.ui.widget.windowInsets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wangdaye.common.base.application.MysplashApplication;

/**
 * Navigation bar view.
 *
 * This view can simulate the height of navigation bar. You can fill the navigation bar by this view.
 *
 * */

public class NavigationBarView extends View {

    @Nullable private ApplyWindowInsetsLayout applyWindowInsetsLayout;
    @NonNull private Rect windowInsets;

    public NavigationBarView(Context context) {
        this(context, null);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyWindowInsetsLayout = null;
        windowInsets = new Rect(-1, -1, -1, -1);
    }

    private boolean ensureApplyWindowInsetsLayout() {
        if (applyWindowInsetsLayout != null) {
            return true;
        }
        View view = this;
        do {
            view = (View) view.getParent();
        } while (view != null && !(view instanceof ApplyWindowInsetsLayout));

        if (view != null) {
            applyWindowInsetsLayout = (ApplyWindowInsetsLayout) view;
            return true;
        }
        return false;
    }

    public void setWindowInsets(@NonNull Rect windowInsets) {
        this.windowInsets.set(windowInsets);
    }

    private boolean isValidWindowInsets() {
        return windowInsets.left != -1
                && windowInsets.top != -1
                && windowInsets.right != -1
                && windowInsets.bottom != -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isValidWindowInsets()) {
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    windowInsets.bottom
            );
        } else if (ensureApplyWindowInsetsLayout()) {
            assert applyWindowInsetsLayout != null;
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    applyWindowInsetsLayout.getWindowInsets().bottom
            );
        } else {
            setMeasuredDimension(
                    MeasureSpec.getSize(widthMeasureSpec),
                    MysplashApplication.getInstance().getWindowInsets().bottom
            );
        }
    }
}
