package com.wangdaye.mysplash.about.model;

import com.wangdaye.mysplash.about.ui.AboutAdapter;

/**
 * Header object.
 *
 * Header in {@link AboutAdapter}.
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
