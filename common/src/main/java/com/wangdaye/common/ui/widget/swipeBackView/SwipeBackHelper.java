package com.wangdaye.common.ui.widget.swipeBackView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.wangdaye.common.R;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;

import java.lang.ref.WeakReference;

class SwipeBackHelper {

    @Nullable private WeakReference<MysplashActivity> previousActivity;
    private ViewGroup currentContentView;
    private View previousDisplayView;
    private boolean swiping;

    SwipeBackHelper() {
        previousActivity = null;
        currentContentView = null;
        previousDisplayView = null;
        swiping = false;
    }

    void prepareViews(MysplashActivity currentActivity) {
        if (swiping) {
            return;
        }
        swiping = true;

        currentContentView = currentActivity.findViewById(Window.ID_ANDROID_CONTENT);

        MysplashActivity secondFloorActivity = MysplashApplication.getInstance().getSecondFloorActivity();
        ViewGroup previousContentView = null;
        if (secondFloorActivity != null) {
            previousContentView = secondFloorActivity.findViewById(Window.ID_ANDROID_CONTENT);
            previousDisplayView = previousContentView.getChildAt(0);
        } else {
            previousDisplayView = LayoutInflater.from(currentActivity).inflate(
                    R.layout.container_null_previous_view, currentContentView, false);
        }

        previousActivity = secondFloorActivity == null
                ? null
                : new WeakReference<>(secondFloorActivity);

        if (previousContentView != null) {
            previousContentView.removeView(previousDisplayView);
        }
        currentContentView.addView(previousDisplayView, 0);
    }

    void clearViews() {
        if (!swiping) {
            return;
        }
        swiping = false;

        if (currentContentView == null) {
            return;
        }

        ViewGroup previewContentView = null;
        if (previousActivity != null
                && previousActivity.get() != null
                && !previousActivity.get().isFinishing()) {
            previewContentView = previousActivity.get().findViewById(Window.ID_ANDROID_CONTENT);
        }

        currentContentView.removeView(previousDisplayView);
        if (previewContentView != null) {
            previewContentView.addView(previousDisplayView);
        }

        previousDisplayView = null;
        currentContentView = null;
    }
}
