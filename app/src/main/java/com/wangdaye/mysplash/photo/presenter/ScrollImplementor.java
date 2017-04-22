package com.wangdaye.mysplash.photo.presenter;

import com.wangdaye.mysplash.common.i.model.ScrollModel;
import com.wangdaye.mysplash.common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash.common.i.view.ScrollView;

/**
 * Scroll implementor.
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
    public void scrollToTop() {
        view.scrollToTop();
    }

    @Override
    public void autoLoad(int dy) {
        view.autoLoad(dy);
    }

    @Override
    public boolean needBackToTop() {
        return view.needBackToTop();
    }
}
