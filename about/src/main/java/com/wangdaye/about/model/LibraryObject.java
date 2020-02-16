package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.adapter.BaseAdapter;

/**
 * Library object.
 *
 * library information in {@link AboutAdapter}.
 *
 * */

public class LibraryObject
        implements AboutModel {

    public int type = AboutModel.TYPE_LIBRARY;
    public String title;
    public String subtitle;
    public String uri;

    public LibraryObject(String title, String subtitle, String uri) {
        this.title = title;
        this.subtitle = subtitle;
        this.uri = uri;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof LibraryObject && uri.equals(((LibraryObject) newModel).uri);
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
