package com.wangdaye.mysplash._common.i.presenter;

/**
 * Load presenter.
 * */

public interface LoadPresenter {

    void setLoadingState();
    void setFailedState();
    void setNormalState();

    int getLoadState();
}
