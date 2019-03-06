package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.about.ui.AboutAdapter;

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
}
