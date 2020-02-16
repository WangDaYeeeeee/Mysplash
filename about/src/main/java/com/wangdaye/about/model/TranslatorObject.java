package com.wangdaye.about.model;

import com.wangdaye.about.ui.AboutAdapter;
import com.wangdaye.common.base.adapter.BaseAdapter;

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

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof TranslatorObject && avatarUrl.equals(((TranslatorObject) newModel).avatarUrl);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return false;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
