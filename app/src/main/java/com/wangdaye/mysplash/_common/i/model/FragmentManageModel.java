package com.wangdaye.mysplash._common.i.model;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Fragment mange model.
 * */

public interface FragmentManageModel {

    List<Fragment> getFragmentList();

    Fragment getFragmentFromList(int position);

    int getFragmentCount();
    void addFragmentToList(Fragment f);
    void popFragmentFromList();
}
