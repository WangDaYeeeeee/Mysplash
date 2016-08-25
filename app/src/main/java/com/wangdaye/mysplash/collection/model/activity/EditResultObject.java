package com.wangdaye.mysplash.collection.model.activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.i.model.EditResultModel;

/**
 * Edit result object.
 * */

public class EditResultObject
        implements EditResultModel {
    // data
    private Object key;

    /** <br> life cycle. */

    public EditResultObject() {
        this.key = Mysplash.getInstance().getCollection();
    }

    /** <br> model. */

    @Override
    public Object getEditKey() {
        return key;
    }

    @Override
    public void setEditKey(Object k) {
        key = k;
    }
}
