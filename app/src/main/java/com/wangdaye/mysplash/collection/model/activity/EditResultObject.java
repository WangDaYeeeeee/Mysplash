package com.wangdaye.mysplash.collection.model.activity;

import com.wangdaye.mysplash.common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash.common.i.model.EditResultModel;

/**
 * Edit result object.
 * */

public class EditResultObject
        implements EditResultModel {

    private Object key;

    public EditResultObject(Collection c) {
        this.key = c;
    }

    @Override
    public Object getEditKey() {
        return key;
    }

    @Override
    public void setEditKey(Object k) {
        key = k;
    }
}
