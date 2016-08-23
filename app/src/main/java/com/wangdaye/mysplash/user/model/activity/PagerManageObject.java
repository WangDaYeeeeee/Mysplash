package com.wangdaye.mysplash.user.model.activity;

import com.wangdaye.mysplash._common.i.model.PagerManageModel;

/**
 * Pager manage object.
 * */

public class PagerManageObject
        implements PagerManageModel {
    // data
    private int pagePosition;

    /** <br> life cycle. */

    public PagerManageObject(int initPosition) {
        this.pagePosition = initPosition;
    }

    /** <br> model. */

    @Override
    public int getPagerPosition() {
        return pagePosition;
    }

    @Override
    public void setPagerPosition(int position) {
        pagePosition = position;
    }
}
