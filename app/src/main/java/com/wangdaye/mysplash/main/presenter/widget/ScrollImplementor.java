package com.wangdaye.mysplash.main.presenter.widget;

import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.ScrollView;

/**
 * Scroll implementor.
 * */

public class ScrollImplementor implements ScrollPresenter {
    // model & view.
    private ScrollView view;

    /** <br> life cycle. */

    public ScrollImplementor(ScrollView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void scrollToTop() {
        view.scrollToTop();
    }

    @Override
    public void autoLoad(int dy) {
        view.autoLoad(dy);
    }
}
