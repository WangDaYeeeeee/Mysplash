package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.common.i.model.ScrollModel;

/**
 * Scroll object.
 *
 * A {@link ScrollModel} for {@link com.wangdaye.mysplash.common.i.view.ScrollView} in
 * {@link com.wangdaye.mysplash.main.view.widget}.
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
