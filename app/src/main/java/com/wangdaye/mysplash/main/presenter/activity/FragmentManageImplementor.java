package com.wangdaye.mysplash.main.presenter.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.model.FragmentManageModel;
import com.wangdaye.mysplash.common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.basic.fragment.MysplashFragment;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.CollectionFragment;
import com.wangdaye.mysplash.main.view.fragment.FollowingFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.main.view.fragment.MultiFilterFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment manage implementor.
 *
 * A {@link FragmentManagePresenter} for
 * {@link com.wangdaye.mysplash.main.view.activity.MainActivity}.
 *
 * */

public class FragmentManageImplementor
        implements FragmentManagePresenter {

    private FragmentManageModel model;

    public FragmentManageImplementor(FragmentManageModel model) {
        this.model = model;
    }

    @Override
    public List<MysplashFragment> getFragmentList(MysplashActivity a, boolean includeHidden) {
        List<Fragment> fragmentList = a.getSupportFragmentManager().getFragments();
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        List<MysplashFragment> resultList = new ArrayList<>();
        for (int i = 0; i < fragmentList.size(); i ++) {
            if (fragmentList.get(i) instanceof MysplashFragment
                    && (includeHidden || !fragmentList.get(i).isHidden())) {
                resultList.add((MysplashFragment) fragmentList.get(i));
            }
        }
        return resultList;
    }

    @Override
    @Nullable
    public MysplashFragment getTopFragment(MysplashActivity a) {
        List<MysplashFragment> list = getFragmentList(a, false);
        if (list.size() > 0) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public void changeFragment(MysplashActivity a, int code) {
        int oldCode = model.getId();
        model.setId(code);

        MysplashFragment newF = null;
        MysplashFragment oldF = null;

        List<MysplashFragment> list = getFragmentList(a, true);
        for (int i = 0; i < list.size(); i ++) {
            if (getFragmentCode(list.get(i)) == oldCode) {
                oldF = list.get(i);
            }
            if (getFragmentCode(list.get(i)) == code) {
                newF = list.get(i);
            }
            if (newF != null && oldF != null) {
                break;
            }
        }
        if (oldF == null) {
            if (newF == null) {
                newF = buildFragmentByCode(code);
            }
            replaceFragment(a, newF);
        } else if (newF == null) {
            newF = buildFragmentByCode(code);
            showAndHideNewFragment(a, newF, oldF);
        } else {
            showAndHideFragment(a, newF, oldF);
        }
    }

    @Override
    public int getId() {
        return model.getId();
    }
/*
    @Override
    public void addFragment(MysplashActivity a, int code) {
        MysplashFragment f = buildFragmentByCode(code);
        model.addFragmentToList(code);

        a.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, f)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
        DisplayUtils.initStatusBarAndNotificationBarStyle(a);
    }

    @Override
    public void popFragment(MysplashActivity a) {
        if (model.getFragmentCount() > 0) {
            MysplashFragment f = getFragmentList(a, false).get(model.getFragmentCount() - 1);
            model.popFragmentFromList();
            a.getSupportFragmentManager().popBackStack();
            f.setStatusBarStyle(f.needSetDarkStatusBar());
        }
    }
*/

    private void replaceFragment(MysplashActivity a, MysplashFragment f) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
        DisplayUtils.setStatusBarStyle(a, false);
    }

    private void showAndHideFragment(MysplashActivity a, MysplashFragment newF, MysplashFragment oldF) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .show(newF)
                .commit();
        newF.initStatusBarStyle();
        newF.initNavigationBarStyle();
    }

    private void showAndHideNewFragment(MysplashActivity a, MysplashFragment newF, MysplashFragment oldF) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.activity_main_fragment, newF)
                .show(newF)
                .commit();
        DisplayUtils.setStatusBarStyle(a, false);
        DisplayUtils.setNavigationBarStyle(a, false, true);
    }

    private MysplashFragment buildFragmentByCode(int code) {
        switch (code) {
            case R.id.action_following:
                return new FollowingFragment();

            case R.id.action_collection:
                return new CollectionFragment();

            case R.id.action_multi_filter:
                return new MultiFilterFragment();

            case R.id.action_category:
                return new CategoryFragment();

            default:
                return new HomeFragment();
        }
    }

    private int getFragmentCode(MysplashFragment f) {
        if (f instanceof HomeFragment) {
            return R.id.action_home;
        } else if (f instanceof FollowingFragment) {
            return R.id.action_following;
        } else if (f instanceof CollectionFragment) {
            return R.id.action_collection;
        } else if (f instanceof MultiFilterFragment) {
            return R.id.action_multi_filter;
        } else { // CategoryFragment.
            return R.id.action_category;
        }
    }
}
