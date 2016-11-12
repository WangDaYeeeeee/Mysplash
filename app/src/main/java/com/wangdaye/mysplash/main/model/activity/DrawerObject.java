package com.wangdaye.mysplash.main.model.activity;

import com.wangdaye.mysplash._common.i.model.DrawerModel;

/**
 * Drawer object.
 * */

public class DrawerObject
        implements DrawerModel {
    // data
    private int selectedId;

    /** <br> life cycle. */

    public DrawerObject(int id) {
        selectedId = id;
    }

    /** <br> model. */

    @Override
    public int getSelectedItemId() {
        return selectedId;
    }

    @Override
    public void setSelectedItemId(int id) {
        selectedId = id;
    }
}
