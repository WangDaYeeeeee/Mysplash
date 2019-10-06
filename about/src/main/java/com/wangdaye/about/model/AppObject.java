package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;

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
}
