package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.common.i.model.ScrollModel;

/**
 * Scroll object.
 *
 * */

public class ScrollObject
        implements ScrollModel {

    private boolean toTop;

    public ScrollObject(boolean top) {
        this.toTop = top;
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
