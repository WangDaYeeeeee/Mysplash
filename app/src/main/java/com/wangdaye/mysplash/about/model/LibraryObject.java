package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.common.i.model.AboutModel;

/**
 * Library object.
 *
 * library information in {@link com.wangdaye.mysplash.common.ui.adapter.AboutAdapter}.
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
}
