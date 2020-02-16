package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.adapter.BaseAdapter;

/**
 * Category object.
 *
 * category in {@link AboutAdapter}.
 *
 * */

public class CategoryObject
        implements AboutModel {

    public int type = AboutModel.TYPE_CATEGORY;
    public String category;

    public CategoryObject(String category) {
        this.category = category;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof CategoryObject && category.equals(((CategoryObject) newModel).category);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return false;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
