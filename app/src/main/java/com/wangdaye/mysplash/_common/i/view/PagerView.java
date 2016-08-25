package com.wangdaye.mysplash._common.i.view;

/**
 * Pager view.
 * */

public interface PagerView {

    void checkToRefresh();
    boolean checkNeedRefresh();
    boolean checkNeedBackToTop();
    void refreshPager();

    void scrollToPageTop();
    void cancelRequest();

    void setKey(String key);
    String getKey();

    boolean canSwipeBack(int dir);
    int getItemCount();
}
