package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common._basic.MysplashFragment;

import java.util.List;

/**
 * Fragment mange model.
 * */

public interface FragmentManageModel {

    List<MysplashFragment> getFragmentList();
    List<Integer> getIdList();

    MysplashFragment getFragmentFromList(int position);

    int getFragmentCount();
    void addFragmentToList(MysplashFragment f, int id);
    void popFragmentFromList();
}
