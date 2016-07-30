package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.main.model.fragment.i.OrderModel;

/**
 * Order object.
 * */

public class OrderObject
        implements OrderModel {
    // data
    private String order;
    private boolean normalMode;

    /** <br> life cycle. */

    public OrderObject(String order, boolean normalMode) {
        this.order = order;
        this.normalMode = normalMode;
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

    // mode.

    @Override
    public boolean isNormalMode() {
        return normalMode;
    }

    @Override
    public void setNormalMode(boolean b) {
        this.normalMode = b;
    }
}
