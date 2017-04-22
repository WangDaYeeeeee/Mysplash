package com.wangdaye.mysplash.collection.presenter.widget;

import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.view.ScrollView;

/**
 * Scroll implementor.
 *
 * A {@link ScrollPresenter} for
 * {@link com.wangdaye.mysplash.collection.view.widget.CollectionPhotosView}.
 *
 * */

public class ScrollImplementor
        implements ScrollPresenter {

    private ScrollModel model;
    private ScrollView view;

    public ScrollImplementor(ScrollModel model, ScrollView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public boolean isToTop() {
        return model.isToTop();
    }

    @Override
    public void setToTop(boolean top) {
        model.setToTop(top);
    }

    @Override
    public boolean needBackToTop() {
        return view.needBackToTop();
    }

    @Override
    public void scrollToTop() {
        model.setToTop(true);
        view.scrollToTop();
    }

    @Override
    public void autoLoad(int dy) {
        view.autoLoad(dy);
    }
}
