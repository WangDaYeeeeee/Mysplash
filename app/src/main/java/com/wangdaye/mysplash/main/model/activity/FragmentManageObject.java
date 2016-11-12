package com.wangdaye.mysplash.main.model.activity;

import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.ui.fragment.SaveInstanceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment mange object.
 * */

public class FragmentManageObject
        implements FragmentManageModel {
    // data
    private List<SaveInstanceFragment> fragmentList;
    private List<Integer> idList;

    /** <br> life cycle. */

    public FragmentManageObject() {
        this.fragmentList = new ArrayList<>();
        this.idList = new ArrayList<>();
    }

    /** <br> model. */

    @Override
    public List<SaveInstanceFragment> getFragmentList() {
        return fragmentList;
    }

    @Override
    public List<Integer> getIdList() {
        return idList;
    }

    @Override
    public SaveInstanceFragment getFragmentFromList(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getFragmentCount() {
        return fragmentList.size();
    }

    @Override
    public void addFragmentToList(SaveInstanceFragment f, int id) {
        fragmentList.add(f);
        idList.add(id);
    }

    @Override
    public void popFragmentFromList() {
        fragmentList.remove(fragmentList.size() - 1);
        idList.remove(idList.size() - 1);
    }
}
