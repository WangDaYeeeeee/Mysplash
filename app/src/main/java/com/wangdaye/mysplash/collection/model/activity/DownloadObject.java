package com.wangdaye.mysplash.collection.model.activity;

import com.wangdaye.mysplash._common.i.model.DownloadModel;

/**
 * Download object.
 * */

public class DownloadObject implements DownloadModel {
    // data
    private Object key;

    /** <br> model. */

    @Override
    public Object getDownloadKey() {
        return key;
    }

    @Override
    public void setDownloadKey(Object key) {
        this.key = key;
    }
}
