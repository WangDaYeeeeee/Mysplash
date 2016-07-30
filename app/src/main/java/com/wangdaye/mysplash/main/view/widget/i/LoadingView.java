package com.wangdaye.mysplash.main.view.widget.i;

import android.view.View;

/**
 * Loading view.
 * */

public interface LoadingView {

    View getLoadingView();
    View getProgressView();
    View getFeedbackContainer();

    void setFeedbackText(String text);
    void showButton();
}
