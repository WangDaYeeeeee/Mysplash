package com.wangdaye.mysplash.main.view.fragment.i;

import com.wangdaye.mysplash.main.view.widget.HomePageView;

/**
 * Pager view.
 * */

public interface PagerView {

    void resetPage(int page, String order);
    HomePageView getPage(int page);
}
