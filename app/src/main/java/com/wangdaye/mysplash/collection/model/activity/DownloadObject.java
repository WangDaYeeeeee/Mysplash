package com.wangdaye.mysplash.collection.model.activity;

import com.wangdaye.mysplash.common.i.model.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject implements DownloadModel {

    private Object key;

    @Override
    public Object getDownloadKey() {
        return key;
    }

    @Override
    public void setDownloadKey(Object key) {
        this.key = key;
    }
}
