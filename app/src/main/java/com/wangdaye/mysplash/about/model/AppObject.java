package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.common.i.model.AboutModel;

/**
 * App object.
 *
 * app information in {@link com.wangdaye.mysplash.common.ui.adapter.AboutAdapter}.
 *
 * */

public class AppObject
        implements AboutModel {
    // data
    public int type = AboutModel.TYPE_APP;
    public int id;
    public int iconId;
    public String text;

    /** <br> life cycle. */

    public AppObject(int id, int iconId, String text) {
        this.id = id;
        this.iconId = iconId;
        this.text = text;
    }

    /** <br> model. */

    @Override
    public int getType() {
        return type;
    }
}
