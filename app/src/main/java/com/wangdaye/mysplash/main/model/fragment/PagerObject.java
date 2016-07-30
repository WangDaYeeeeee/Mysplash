package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.main.model.fragment.i.PagerModel;

/**
 * Pager object.
 * */

public class PagerObject
        implements PagerModel {
    // data
    private int page = 0;

    /** <br> model. */

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int p) {
        this.page = p;
    }
}
