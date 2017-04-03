package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.common.i.model.AboutModel;

/**
 * Category object.
 *
 * category in {@link com.wangdaye.mysplash.common.ui.adapter.AboutAdapter}.
 *
 * */

public class CategoryObject
        implements AboutModel {
    // data
    public int type = AboutModel.TYPE_CATEGORY;
    public String category;

    /** <br> life cycle. */

    public CategoryObject(String category) {
        this.category = category;
    }

    /** <br> model. */

    @Override
    public int getType() {
        return type;
    }
}
