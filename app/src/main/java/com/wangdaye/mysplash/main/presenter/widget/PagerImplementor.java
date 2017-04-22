package com.wangdaye.mysplash.main.presenter.widget;

import com.wangdaye.mysplash.common.i.presenter.PagerPresenter;
import com.wangdaye.mysplash.common.i.view.PagerView;

/**
 * Pager implementor.
 *
 * A {@link PagerPresenter} for {@link PagerView} in
 * {@link com.wangdaye.mysplash.main.view.widget.HomePhotosView},
 * {@link com.wangdaye.mysplash.main.view.widget.HomeSearchView},
 * {@link com.wangdaye.mysplash.main.view.widget.HomeCollectionsView}.
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
