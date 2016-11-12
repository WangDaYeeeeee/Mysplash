package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.wangdaye.mysplash._common.ui.fragment.SaveInstanceFragment;

import java.util.List;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    List<SaveInstanceFragment> getFragmentList();
    SaveInstanceFragment getTopFragment();

    List<Integer> getIdList();
    void clearIdList();

    void changeFragment(Activity a, Bundle saveInstanceState, int code);
    void addFragment(Activity a, Bundle saveInstanceState, int code);
    void popFragment(Activity a);

    int getFragmentCount();
}
