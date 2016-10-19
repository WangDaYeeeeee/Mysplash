package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash._common.i.model.AboutModel;

/**
 * Category about object.
 * */

public class CategoryAboutObject
        implements AboutModel {
    // data
    public int type = AboutModel.TYPE_CATEGORY;
    public String category;

    /** <br> life cycle. */

    public CategoryAboutObject(String category) {
        this.category = category;
    }

    /** <br> model. */

    @Override
    public int getType() {
        return type;
    }
}
