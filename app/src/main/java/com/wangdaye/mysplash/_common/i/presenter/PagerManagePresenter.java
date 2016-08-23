package com.wangdaye.mysplash._common.i.presenter;

import com.wangdaye.mysplash._common.i.view.PagerView;

/**
 * Pager manage presenter.
 * */

public interface PagerManagePresenter {

    int getPagerPosition();
    void setPagerPosition(int position);

    PagerView getPagerView(int position);
    void checkToRefresh(int position);

    void pageScrollToTop();
    String getPageKey(int position);

    boolean canPagerSwipeBack(int dir);
    int getPagerItemCount();
}
