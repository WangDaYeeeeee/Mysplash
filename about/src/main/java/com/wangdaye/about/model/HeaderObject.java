package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.adapter.BaseAdapter;

/**
 * Header object.
 *
 * Header in {@link AboutAdapter}.
 *
 * */

public class HeaderObject
        implements AboutModel {

    public int type = TYPE_HEADER;

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof HeaderObject;
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
