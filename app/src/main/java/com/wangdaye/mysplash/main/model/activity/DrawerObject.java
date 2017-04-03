package com.wangdaye.mysplash.main.model.activity;

import com.wangdaye.mysplash.common.i.model.DrawerModel;

/**
 * Drawer object.
 * */

public class DrawerObject
        implements DrawerModel {
    // data
    private int checkedId;

    /** <br> life cycle. */

    public DrawerObject(int id) {
        checkedId = id;
    }

    /** <br> model. */

    @Override
    public int getCheckedItemId() {
        return checkedId;
    }

    @Override
    public void setCheckedItemId(int id) {
        checkedId = id;
    }
}
