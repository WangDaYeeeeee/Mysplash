package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.main.model.fragment.i.CategoryModel;

/**
 * Category object.
 * */

public class CategoryObject
        implements CategoryModel {
    // data
    private int categoryId;

    /** <br> life cycle. */

    public CategoryObject(int id) {
        this.categoryId = id;
    }

    /** <br> model. */

    @Override
    public int getCategoryId() {
        return categoryId;
    }
}
