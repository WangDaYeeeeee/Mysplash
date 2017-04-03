package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.i.model.CategoryManageModel;

/**
 * Category manage object.
 * */

public class CategoryManageObject
        implements CategoryManageModel {
    // data
    @Mysplash.CategoryIdRule
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