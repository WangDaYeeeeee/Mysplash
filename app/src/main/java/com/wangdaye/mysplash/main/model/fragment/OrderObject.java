package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.main.model.fragment.i.OrderModel;

/**
 * Order object.
 * */

public class OrderObject
        implements OrderModel {
    // data
    private String order;

    /** <br> life cycle. */

    public OrderObject(String order) {
        this.order = order;
    }

    /** <br> model. */

    // order.

    @Override
    public String getOrder() {
        return order;
    }

    @Override
    public void setOrder(String o) {
        this.order = o;
    }
}
