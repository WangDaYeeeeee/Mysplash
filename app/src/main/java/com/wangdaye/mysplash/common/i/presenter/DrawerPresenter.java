package com.wangdaye.mysplash.common.i.presenter;

/**
 * Drawer presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.DrawerView}.
 *
 * */

public interface DrawerPresenter {

    void touchNavItem(int id);
    int getCheckedItemId();
}
