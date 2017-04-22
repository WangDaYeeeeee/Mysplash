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

    public int type = AboutModel.TYPE_HEADER;

    @Override
    public int getType() {
        return type;
    }
}
