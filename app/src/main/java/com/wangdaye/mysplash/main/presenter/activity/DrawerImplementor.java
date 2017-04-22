package com.wangdaye.mysplash.main.presenter.activity;

import com.wangdaye.mysplash.common.i.model.DrawerModel;
import com.wangdaye.mysplash.common.i.presenter.DrawerPresenter;
import com.wangdaye.mysplash.common.i.view.DrawerView;

/**
 * Drawer implementor.
 *
 * A {@link DrawerPresenter} for {@link com.wangdaye.mysplash.main.view.activity.MainActivity}.
 *
 * */

public class DrawerImplementor
        implements DrawerPresenter {

    private DrawerModel model;
    private DrawerView view;

    public DrawerImplementor(DrawerModel model, DrawerView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void touchNavItem(int id) {
        int oldId = model.getCheckedItemId();
        if (oldId != id) {
            view.touchNavItem(id);
        }
    }

    @Override
    public int getCheckedItemId() {
        return model.getCheckedItemId();
    }

    @Override
    public void setCheckedItemId(int id) {
        model.setCheckedItemId(id);
        view.setCheckedItem(id);
    }
}
