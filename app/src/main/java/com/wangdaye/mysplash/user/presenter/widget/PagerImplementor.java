package com.wangdaye.mysplash.user.presenter.widget;

import com.wangdaye.mysplash._common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash._common.i.view.PagerView;

/**
 * Pager implementor.
 * */

public class PagerImplementor
        implements PagerPresenter {
    // model & view.
    private PagerView view;

    /** <br> life cycle. */

    public PagerImplementor(PagerView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public boolean checkNeedRefresh() {
        return view.checkNeedRefresh();
    }

    @Override
    public void refreshPager() {
        view.refreshPager();
    }
}
