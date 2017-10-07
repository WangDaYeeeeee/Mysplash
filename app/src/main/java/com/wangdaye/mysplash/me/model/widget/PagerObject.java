package com.wangdaye.mysplash.me.model.widget;

import com.wangdaye.mysplash.common.i.model.PagerModel;

/**
 * Pager object.
 * */

public class PagerObject
        implements PagerModel {

    private int index;
    private boolean selected;

    public PagerObject(int index, boolean selected) {
        this.index = index;
        this.selected = selected;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
