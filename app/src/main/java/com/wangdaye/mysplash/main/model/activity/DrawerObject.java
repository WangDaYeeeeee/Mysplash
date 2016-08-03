package com.wangdaye.mysplash.main.model.activity;

import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.main.model.activity.i.DrawerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Drawer object.
 * */

public class DrawerObject
        implements DrawerModel {
    // data
    private List<Fragment> fragmentList = new ArrayList<>();
    private int menuItemId = R.id.action_home;

    public static final int HOME_FRAGMENT = 1;
    public static final int CHANGE_THEME = -1;
    public static final int SETTINGS_ACTIVITY = -2;
    public static final int ABOUT_ACTIVITY = -3;

    /** <br> model. */

    // menu.

    @Override
    public void setMenuItemId(int id) {
        this.menuItemId = id;
    }

    @Override
    public int getMenuItemId() {
        return menuItemId;
    }

    // fragment.

    @Override
    public void addFragmentToList(Fragment f) {
        fragmentList.add(f);
    }

    @Override
    public void removeFragmentFromList() {
        fragmentList.remove(fragmentList.size() - 1);
    }

    @Override
    public int getFragmentCount() {
        return fragmentList.size();
    }
}
