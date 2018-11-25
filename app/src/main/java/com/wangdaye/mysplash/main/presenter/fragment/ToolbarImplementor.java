package com.wangdaye.mysplash.main.presenter.fragment;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.basic.fragment.MysplashFragment;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;

/**
 * Toolbar implementor.
 *
 * A {@link ToolbarPresenter} for the views in {@link com.wangdaye.mysplash.main.view.fragment}.
 *
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        DrawerLayout drawer = a.findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void touchToolbar(MysplashActivity a) {
        MysplashFragment fragment = ((MainActivity) a).getTopFragment();
        if (fragment != null) {
            fragment.backToTop();
        }
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        MainActivity activity = (MainActivity) a;
        switch (itemId) {
            case R.id.action_search:
                IntentHelper.startSearchActivity(a, null);
                break;

            case R.id.action_filter:
                MysplashFragment f = activity.getTopFragment();
                if (f instanceof HomeFragment) {
                    ((HomeFragment) f).showPopup();
                }
                break;
        }
        return true;
    }
}
