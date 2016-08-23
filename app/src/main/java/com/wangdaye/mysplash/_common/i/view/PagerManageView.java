package com.wangdaye.mysplash._common.i.view;

/**
 * Pager manage view.
 * */

public interface PagerManageView {

    PagerView getPagerView(int position);
    boolean canPagerSwipeBack(int position, int dir);
    int getPagerItemCount(int position);
}
