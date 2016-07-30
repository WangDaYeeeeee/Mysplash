package com.wangdaye.mysplash.main.model.activity.i;


import android.support.v4.app.Fragment;

/**
 * Fragment manage model.
 * */

public interface FragmentManageModel {

    void setMenuItemId(int id);
    int getMenuItemId();

    void addFragmentToList(Fragment f);
    void removeFragmentFromList();
    int getFragmentCount();
}
