package com.wangdaye.mysplash.common.i.presenter;

import androidx.annotation.NonNull;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;

/**
 * Load presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.LoadView}.
 *
 * */

public interface LoadPresenter {

    void bindActivity(@NonNull MysplashActivity activity);

    int getLoadState();

    void setLoadingState();
    void setFailedState();
    void setNormalState();
}
