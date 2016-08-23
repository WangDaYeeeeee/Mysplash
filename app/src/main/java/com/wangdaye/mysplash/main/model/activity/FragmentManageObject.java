package com.wangdaye.mysplash.main.model.activity;

import android.support.v4.app.Fragment;

import com.wangdaye.mysplash._common.i.model.FragmentManageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment mange object.
 * */

public class FragmentManageObject
        implements FragmentManageModel {
    // data
    private List<Fragment> fragmentList;
    private int fragmentCode;

    public static final int NULL_CODE = 0;

    /** <br> life cycle. */

    public FragmentManageObject(int code) {
        this.fragmentList = new ArrayList<>();
        this.fragmentCode = code;
    }

    /** <br> model. */

    // code.

    @Override
    public int getFragmentCode() {
        return fragmentCode;
    }

    @Override
    public void setFragmentCode(int code) {
        fragmentCode = code;
    }

    // fragment list.

    @Override
    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    @Override
    public Fragment getFragmentFromList(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getFragmentCount() {
        return fragmentList.size();
    }

    @Override
    public void addFragmentToList(Fragment f) {
        fragmentList.add(f);
    }

    @Override
    public void popFragmentFromList() {
        fragmentList.remove(fragmentList.size() - 1);
    }
}
