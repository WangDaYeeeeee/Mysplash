package com.wangdaye.common.ui.widget.swipeBackView;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.base.application.MysplashApplication;

import java.lang.ref.WeakReference;

class SwipeBackHelper {

    private WeakReference<MysplashActivity> previousActivity;
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
        if (secondFloorActivity == null) {
            return;
        }

        ViewGroup previousContentView = secondFloorActivity.findViewById(Window.ID_ANDROID_CONTENT);
        previousDisplayView = previousContentView.getChildAt(0);

        if (previousDisplayView == null) {
            currentContentView = null;
            return;
        }

        previousActivity = new WeakReference<>(secondFloorActivity);

        previousContentView.removeView(previousDisplayView);
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
                && previousActivity.get() != null) {
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
