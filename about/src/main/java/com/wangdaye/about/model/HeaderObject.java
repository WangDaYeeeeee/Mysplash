package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;

/**
 * Header object.
 *
 * Header in {@link AboutAdapter}.
 *
 * */

public class HeaderObject
        implements AboutModel {

    public int type = TYPE_HEADER;

    @Override
    public int getType() {
        return type;
    }
}
