package com.wangdaye.mysplash.common.i.view;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Drawer view.
 *
 * A view which has {@link DrawerLayout}
 * and {@link NavigationView}.
 *
 * */

public interface DrawerView {

    void touchNavItem(int id);
    void setCheckedItem(int id);
}
