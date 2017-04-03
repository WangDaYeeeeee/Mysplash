package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.common.i.model.AboutModel;

/**
 * Header object.
 *
 * Header in {@link com.wangdaye.mysplash.common.ui.adapter.AboutAdapter}.
 *
 * */

public class HeaderObject
        implements AboutModel {
    // data
    public int type = AboutModel.TYPE_HEADER;

    /** <br> model. */

    @Override
    public int getType() {
        return type;
    }
}
