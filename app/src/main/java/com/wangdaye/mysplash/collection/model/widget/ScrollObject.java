package com.wangdaye.mysplash.collection.model.widget;

import com.wangdaye.mysplash._common.i.model.ScrollModel;

/**
 * Scroll object.
 * */

public class ScrollObject
        implements ScrollModel {
    // data
    private boolean toTop;

    /** <br> life cycle. */

    public ScrollObject() {
        this.toTop = true;
    }

    /** <br> model. */

    @Override
    public boolean isToTop() {
        return toTop;
    }

    @Override
    public void setToTop(boolean top) {
        toTop = top;
    }
}
