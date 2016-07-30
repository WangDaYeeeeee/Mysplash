package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.main.model.widget.i.TypeStateModel;

/**
 * Type state object.
 * */

public class TypeStateObject
        implements TypeStateModel {
    // data
    private int type;
    public static final int NEW_TYPE = 1;
    public static final int FEATURED_TYPE = 2;
    //public static final int COLLECTIONS_TYPE = 3;

    /** <br> life cycle. */

    public TypeStateObject(int type) {
        this.type = type;
    }

    /** <br> model. */

    @Override
    public int getType() {
        return type;
    }
}
