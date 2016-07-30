package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash.main.model.fragment.i.OrderModel;
import com.wangdaye.mysplash.main.model.fragment.i.PagerModel;
import com.wangdaye.mysplash.main.presenter.fragment.i.PagerPresenter;
import com.wangdaye.mysplash.main.view.fragment.i.PagerView;
import com.wangdaye.mysplash.main.view.widget.HomePageView;

/**
 * Pager implementor.
 * */

public class PagerImp
        implements PagerPresenter {
    // model.
    private OrderModel orderModel;
    private PagerModel pagerModel;

    // view.
    private PagerView pagerView;

    /** <br> life cycle. */

    public PagerImp(OrderModel orderModel, PagerModel pagerModel,
                    PagerView pagerView) {
        this.orderModel = orderModel;
        this.pagerModel = pagerModel;
        this.pagerView = pagerView;
    }

    @Override
    public void checkPageRefresh(int pageTo) {
        pagerModel.setPage(pageTo);
        HomePageView v = pagerView.getPage(pageTo);
        if (v.isNeedChangeOrder(orderModel.getOrder()) || v.isNeedRefresh()) {
            pagerView.resetPage(pageTo, orderModel.getOrder());
        }
    }
}
