package com.wangdaye.mysplash.me.presenter.activity;

import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.ToolbarView;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {
    // model & view.
    private ToolbarView view;

    /** <br> */

    public ToolbarImplementor(ToolbarView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon() {
        view.touchNavigatorIcon();
    }

    @Override
    public void touchToolbar() {
        view.touchToolbar();
    }

    @Override
    public void touchMenuItem(int itemId) {
        view.touchMenuItem(itemId);
    }
}
