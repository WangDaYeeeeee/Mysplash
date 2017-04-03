package com.wangdaye.mysplash.common.i.presenter;

import com.wangdaye.mysplash.Mysplash;

/**
 * Category manage presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.CategoryManageView}.
 *
 * */

public interface CategoryManagePresenter {

    @Mysplash.CategoryIdRule
    int getCategoryId();
    void setCategoryId(@Mysplash.CategoryIdRule int id);
}
