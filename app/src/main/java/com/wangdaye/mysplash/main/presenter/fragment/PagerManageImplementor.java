package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash._common.i.model.PagerManageModel;
import com.wangdaye.mysplash._common.i.presenter.PagerManagePresenter;
import com.wangdaye.mysplash._common.i.view.PagerManageView;
import com.wangdaye.mysplash._common.i.view.PagerView;

/**
 * Pager manage implementor.
 * */

public class PagerManageImplementor
        implements PagerManagePresenter {
    // model & view.
    private PagerManageModel model;
    private PagerManageView view;

    /** <br> life cycle. */

    public PagerManageImplementor(PagerManageModel model, PagerManageView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public int getPagerPosition() {
        return model.getPagerPosition();
    }

    @Override
    public void setPagerPosition(int position) {
        model.setPagerPosition(position);
    }

    @Override
    public PagerView getPagerView(int position) {
        return view.getPagerView(position);
    }

    @Override
    public void checkToRefresh(int position) {
        getPagerView(position).checkToRefresh();
    }

    @Override
    public boolean needPagerBackToTop() {
        return view.getPagerView(model.getPagerPosition()).checkNeedBackToTop();
    }

    @Override
    public void pagerScrollToTop() {
        getPagerView(model.getPagerPosition()).scrollToPageTop();
    }

    @Override
    public String getPagerKey(int position) {
        return getPagerView(position).getKey();
    }

    @Override
    public boolean canPagerSwipeBack(int dir) {
        return false;
    }

    @Override
    public int getPagerItemCount() {
        return view.getPagerItemCount(model.getPagerPosition());
    }
}
