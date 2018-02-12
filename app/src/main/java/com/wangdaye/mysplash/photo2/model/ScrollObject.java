package com.wangdaye.mysplash.photo2.model;

import com.wangdaye.mysplash.common.i.model.ScrollModel;

/**
 * Scroll object.
 * */

public class ScrollObject
        implements ScrollModel {

    private boolean toTop;

    public ScrollObject() {
        this.toTop = true;
    }

    @Override
    public boolean isToTop() {
        return toTop;
    }

    @Override
    public void setToTop(boolean top) {
        toTop = top;
    }
}
