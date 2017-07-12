package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.i.model.CategoryManageModel;

/**
 * Category manage object.
 *
 * */

public class CategoryManageObject
        implements CategoryManageModel {

    @Mysplash.CategoryIdRule
    private int categoryId;

    public CategoryManageObject(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public void setCategoryId(int id) {
        categoryId = id;
    }
}