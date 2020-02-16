package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.adapter.BaseAdapter;

/**
 * App object.
 *
 * app information in {@link AboutAdapter}.
 *
 * */

public class AppObject
        implements AboutModel {

    public int type = TYPE_APP;
    public int id;
    public int iconId;
    public String text;

    public AppObject(int id, int iconId, String text) {
        this.id = id;
        this.iconId = iconId;
        this.text = text;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof AppObject && ((AppObject) newModel).iconId == iconId;
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
