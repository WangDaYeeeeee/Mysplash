package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash._common.i.model.CategoryManageModel;
import com.wangdaye.mysplash._common.i.presenter.CategoryManagePresenter;
import com.wangdaye.mysplash._common.i.view.CategoryManageView;

/**
 * Category manage implementor.
 * */

public class CategoryManageImplementor
        implements CategoryManagePresenter {
    // model & view.
    private CategoryManageModel model;
    private CategoryManageView view;

    /** <br> life cycle. */

    public CategoryManageImplementor(CategoryManageModel model, CategoryManageView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public int getCategoryId() {
        return model.getCategoryId();
    }

    @Override
    public void setCategoryId(int id) {
        model.setCategoryId(id);
        view.setCategory(id);
    }
}