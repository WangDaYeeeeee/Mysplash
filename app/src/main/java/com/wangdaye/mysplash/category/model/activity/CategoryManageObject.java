package com.wangdaye.mysplash.category.model.activity;

import com.wangdaye.mysplash._common.i.model.CategoryManageModel;

/**
 * Category manage object.
 * */

public class CategoryManageObject
        implements CategoryManageModel {
    // data
    private int categoryId;

    /** <br> life cycle. */

    public CategoryManageObject(int categoryId) {
        this.categoryId = categoryId;
    }

    /** <br> model. */

    @Override
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public void setCategoryId(int id) {
        categoryId = id;
    }
}