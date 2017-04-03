package com.wangdaye.mysplash.common.i.presenter;

/**
 * Load presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.LoadView}.
 *
 * */

public interface LoadPresenter {

    void setLoadingState();
    void setFailedState();
    void setNormalState();

    int getLoadState();
}
