package com.wangdaye.mysplash.main.model.activity;

import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.main.model.activity.i.FragmentManageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment manage object.
 * */

public class FragmentManageObject
        implements FragmentManageModel {
    // data
    private List<Fragment> fragmentList = new ArrayList<>();
    private int menuItemId = R.id.action_home;

    public static final int HOME_FRAGMENT = 1;
    public static final int SETTINGS_ACTIVITY = -1;
    public static final int ABOUT_ACTIVITY = -2;

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
