package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash._common.i.model.AboutModel;

/**
 * About object.
 * */

public class HeaderAboutObject
        implements AboutModel {
    // data
    public int type = AboutModel.TYPE_HEADER;

    /** <br> model. */

    @Override
    public int getType() {
        return type;
    }
}
