package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash._common.i.model.AboutModel;

/**
 * App about object.
 * */

public class AppAboutObject
        implements AboutModel {
    // data
    public int type = AboutModel.TYPE_APP;
    public int id;
    public int iconId;
    public String text;

    /** <br> life cycle. */

    public AppAboutObject(int id, int iconId, String text) {
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
