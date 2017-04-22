package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common.i.view.PagerView;

/**
 * Pager presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.PagerView}.
 *
 * */

public interface PagerPresenter {

    /**
     * Check the {@link com.wangdaye.mysplash.common.i.view.PagerView} need to execute the
     * {@link PagerView#refreshPager()} method.
     * */
    boolean checkNeedRefresh();
    void refreshPager();
}
