package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.i.view.PagerView;

/**
 * Pager manage presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.PagerManageView}.
 *
 * */

public interface PagerManagePresenter {

    int getPagerPosition();
    void setPagerPosition(int position);

    PagerView getPagerView(int position);
    void checkToRefresh(int position);

    /**
     * {@link MysplashActivity#backToTop()}
     * {@link com.wangdaye.mysplash.common.utils.BackToTopUtils}
     * */
    boolean needPagerBackToTop();
    void pagerScrollToTop();

    /**
     * Get the key word of the {@link PagerView}, like the order of photos, or type of collections.
     *
     * @return Key words.
     * */
    String getPagerKey(int position);

    /**
     * Check {@link PagerView} can swipe back.
     *
     * @return Can swipe back.
     * */
    boolean canPagerSwipeBack(int dir);

    int getPagerItemCount();
}
