package com.wangdaye.mysplash.main.model.activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.DrawerModel;

/**
 * Drawer object.
 * */

public class DrawerObject
        implements DrawerModel {
    // data
    private int selectedId;

    /** <br> life cycle. */

    public DrawerObject() {
        selectedId = R.id.action_home;
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
