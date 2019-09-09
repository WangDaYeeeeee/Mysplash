package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;

/**
 * Translator object.
 *
 * translator information in {@link AboutAdapter}.
 *
 * */

public class TranslatorObject
        implements AboutModel {

    public int type = TYPE_TRANSLATOR;
    public String avatarUrl;
    public String title;
    public int flagId;
    public String subtitle;

    public TranslatorObject(String avatarUrl, String title, int flagId, String subtitle) {
        this.avatarUrl = avatarUrl;
        this.title = title;
        this.flagId = flagId;
        this.subtitle = subtitle;
    }

    @Override
    public int getType() {
        return type;
    }
}
