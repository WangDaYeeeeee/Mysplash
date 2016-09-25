package com.wangdaye.mysplash.main.presenter.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(Activity a) {
        DrawerLayout drawer = (DrawerLayout) a.findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void touchToolbar(Activity a) {
        Fragment f = ((MainActivity) a).getTopFragment();
        if (f instanceof HomeFragment) {
            ((HomeFragment) f).pagerBackToTop();
        } else {
            ((CategoryFragment) f).pagerBackToTop();
        }
    }

    @Override
    public boolean touchMenuItem(Activity a, int itemId) {
        MainActivity activity = (MainActivity) a;
        switch (itemId) {
            case R.id.action_search:
                activity.insertFragment(itemId);
                break;

            case R.id.action_filter:
                Fragment f = activity.getTopFragment();

                if (f instanceof HomeFragment) {
                    ((HomeFragment) f).showPopup();
                } else {
                    ((CategoryFragment) f).showPopup();
                }
                break;
        }
        return true;
    }
}
