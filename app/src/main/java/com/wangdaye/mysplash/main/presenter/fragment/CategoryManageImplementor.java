package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash.common.i.model.CategoryManageModel;
import com.wangdaye.mysplash.common.i.presenter.CategoryManagePresenter;
import com.wangdaye.mysplash.common.i.view.CategoryManageView;

/**
 * Category manage implementor.
 *
 * A {@link CategoryManagePresenter} for
 * {@link com.wangdaye.mysplash.main.view.fragment.CategoryFragment}.
 *
 * */

public class CategoryManageImplementor
        implements CategoryManagePresenter {

    private CategoryManageModel model;
    private CategoryManageView view;

    public CategoryManageImplementor(CategoryManageModel model, CategoryManageView view) {
        this.model = model;
        this.view = view;
    }

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