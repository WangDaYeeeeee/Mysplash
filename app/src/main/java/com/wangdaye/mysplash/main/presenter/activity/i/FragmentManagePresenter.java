package com.wangdaye.mysplash.main.presenter.activity.i;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Fragment manage presenter.
 * */

public interface FragmentManagePresenter {

    void selectDrawerItem(int id);

    void changeFragment(Fragment f);
    void addFragment(Fragment f);
    void removeFragment();
    void clearFragment();

    void processMessage(Context c, int what);
}
