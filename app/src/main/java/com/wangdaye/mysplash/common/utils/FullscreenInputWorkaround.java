package com.wangdaye.mysplash.common.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

public class FullscreenInputWorkaround {
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static FullscreenInputWorkaround assistActivity(Activity activity, View contentView,
                                                           InputShowListener inputShowListener) {
        return new FullscreenInputWorkaround(activity, contentView, inputShowListener);
    }

    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams layoutParams;

    private FullscreenInputWorkaround(Activity activity, View contentView, InputShowListener inputShowListener) {
        this.activity = activity;
        this.inputShowListener = inputShowListener;
        mChildOfContent = contentView;
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(this::possiblyResizeChildOfContent);
        layoutParams = mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();

            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                layoutParams.height = usableHeightSansKeyboard - heightDifference;
                if (inputShowListener != null) {
                    inputShowListener.inputShow(true);
                }
            } else {
                // keyboard probably just became hidden
                layoutParams.height = usableHeightSansKeyboard;
                if (inputShowListener != null) {
                    inputShowListener.inputShow(false);
                }
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (r.bottom - r.top) + statusBarHeight;
        }

        return (r.bottom - r.top);
    }

    private InputShowListener inputShowListener;

    public interface InputShowListener {
        void inputShow(boolean show);
    }
}
