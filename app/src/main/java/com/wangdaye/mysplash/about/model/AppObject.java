package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.about.ui.AboutAdapter;

/**
 * App object.
 *
 * app information in {@link AboutAdapter}.
 *
 * */

public class AppObject
        implements AboutModel {

    public int type = AboutModel.TYPE_APP;
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
