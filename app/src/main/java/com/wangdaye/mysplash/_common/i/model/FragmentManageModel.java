package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.ui.fragment.SaveInstanceFragment;

import java.util.List;

/**
 * Fragment mange model.
 * */

public interface FragmentManageModel {

    List<SaveInstanceFragment> getFragmentList();
    List<Integer> getIdList();

    SaveInstanceFragment getFragmentFromList(int position);

    int getFragmentCount();
    void addFragmentToList(SaveInstanceFragment f, int id);
    void popFragmentFromList();
}
