package com.wangdaye.mysplash.tag.model.widget;

import com.wangdaye.mysplash.common.i.model.ScrollModel;

/**
 * Scroll object.
 * */

public class ScrollObject
        implements ScrollModel {
    // data
    private boolean toTop;

    /** <br> life cycle. */

    public ScrollObject(boolean top) {
        this.toTop = top;
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
