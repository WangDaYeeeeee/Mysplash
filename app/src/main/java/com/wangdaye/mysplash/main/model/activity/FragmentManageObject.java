package com.wangdaye.mysplash.main.model.activity;

import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.ui.fragment.MysplashFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment mange object.
 * */

public class FragmentManageObject
        implements FragmentManageModel {
    // data
    private List<MysplashFragment> fragmentList;
    private List<Integer> idList;

    /** <br> life cycle. */

    public FragmentManageObject() {
        this.fragmentList = new ArrayList<>();
        this.idList = new ArrayList<>();
    }

    /** <br> model. */

    @Override
    public List<MysplashFragment> getFragmentList() {
        return fragmentList;
    }

    @Override
    public List<Integer> getIdList() {
        return idList;
    }

    @Override
    public MysplashFragment getFragmentFromList(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getFragmentCount() {
        return fragmentList.size();
    }

    @Override
    public void addFragmentToList(MysplashFragment f, int id) {
        fragmentList.add(f);
        idList.add(id);
    }

    @Override
    public void popFragmentFromList() {
        fragmentList.remove(fragmentList.size() - 1);
        idList.remove(idList.size() - 1);
    }
}
