package com.wangdaye.mysplash._common.i.model;

import java.util.List;

/**
 * Fragment mange model.
 * */

public interface FragmentManageModel {

    List<Integer> getIdList();

    int getFragmentCount();
    void addFragmentToList(int id);
    void popFragmentFromList();
}
