package com.wangdaye.mysplash.main.presenter.fragment;

import android.support.annotation.Nullable;

import com.wangdaye.mysplash.main.model.fragment.i.PagerModel;
import com.wangdaye.mysplash.main.presenter.fragment.i.ToolbarPresenter;
import com.wangdaye.mysplash.main.view.fragment.i.ToolbarView;

/**
 * Toolbar implementor.
 * */

public class ToolbarImp
        implements ToolbarPresenter {
    // model.
    private PagerModel pagerModel;

    // view.
    private ToolbarView toolbarView;

    /** <br> life cycle. */

    public ToolbarImp(@Nullable PagerModel pagerModel, ToolbarView toolbarView) {
        this.pagerModel = pagerModel;
        this.toolbarView = toolbarView;
    }

    /** <br> presenter. */

    @Override
    public void clickNavigationIcon() {
        toolbarView.clickNavigationIcon();
    }

    @Override
    public void clickToolbar() {
        if (pagerModel == null) {
            toolbarView.scrollToTop(0);
        } else {
            toolbarView.scrollToTop(pagerModel.getPage());
        }
    }
}
