package com.wangdaye.mysplash.common.i.model;

/**
 * Pager model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.PagerView}.
 * */

public interface PagerModel {

    int getIndex();

    boolean isSelected();
    void setSelected(boolean selected);
}
