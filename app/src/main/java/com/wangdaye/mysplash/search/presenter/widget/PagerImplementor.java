package com.wangdaye.mysplash.search.presenter.widget;

import com.wangdaye.mysplash.common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash.common.i.view.PagerView;

/**
 * Pager implementor.
 *
 * */

public class PagerImplementor
        implements PagerPresenter {

    private PagerView view;

    public PagerImplementor(PagerView view) {
        this.view = view;
    }

    @Override
    public boolean checkNeedRefresh() {
        return view.checkNeedRefresh();
    }

    @Override
    public void refreshPager() {
        view.refreshPager();
    }
}
