package com.wangdaye.mysplash.main.presenter.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common._basic.MysplashFragment;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.FollowingFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.main.view.fragment.MultiFilterFragment;
import com.wangdaye.mysplash.main.view.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment manage implementor.
 * */

public class FragmentManageImplementor
        implements FragmentManagePresenter {
    // model & view.
    private FragmentManageModel model;

    /** <br> life cycle. */

    public FragmentManageImplementor(FragmentManageModel model) {
        this.model = model;
    }

    @Override
    public List<MysplashFragment> getFragmentList(MysplashActivity a, boolean includeHidden) {
        List<Fragment> fragmentList = a.getSupportFragmentManager().getFragments();
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        List<MysplashFragment> resultList = new ArrayList<>(model.getFragmentCount());
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
        if (list.size() == getFragmentCount()) {
            return list.get(getFragmentCount() - 1);
        } else {
            return null;
        }
    }

    @Override
    public List<Integer> getIdList() {
        return model.getIdList();
    }

    @Override
    public void clearList() {
        model.getIdList().clear();
    }

    /** <br> presenter. */

    @Override
    public void changeFragment(MysplashActivity a, int code, boolean init) {
        int oldCode = model.getIdList().get(0);
        clearList();
        model.addFragmentToList(code);

        if (init) {
            MysplashFragment f = buildFragmentByCode(code);
            replaceFragment(a, f);
        } else {
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
            if (newF == null) {
                newF = buildFragmentByCode(code);
                showAndHideNewFragment(a, newF, oldF);
            } else {
                showAndHideFragment(a, newF, oldF);
            }
        }
    }

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
        DisplayUtils.initStatusBarStyle(a);
    }

    @Override
    public void popFragment(MysplashActivity a) {
        if (model.getFragmentCount() > 0) {
            MysplashFragment f = getFragmentList(a, false).get(model.getFragmentCount() - 1);
            model.popFragmentFromList();
            a.getSupportFragmentManager().popBackStack();
            f.setStatusBarStyle(f.needSetOnlyWhiteStatusBarText());
        }
    }

    @Override
    public int getFragmentCount() {
        return model.getFragmentCount();
    }

    /** <br> utils. */

    private void replaceFragment(MysplashActivity a, MysplashFragment f) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
        DisplayUtils.initStatusBarStyle(a);
    }

    private void showAndHideFragment(MysplashActivity a, MysplashFragment newF, MysplashFragment oldF) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .show(newF)
                .commit();
        newF.setStatusBarStyle(newF.needSetOnlyWhiteStatusBarText());
    }

    private void showAndHideNewFragment(MysplashActivity a, MysplashFragment newF, MysplashFragment oldF) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .hide(oldF)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.activity_main_fragment, newF)
                .show(newF)
                .commit();
        DisplayUtils.initStatusBarStyle(a);
    }

    private MysplashFragment buildFragmentByCode(int code) {
        switch (code) {
            case R.id.action_search:
                return new SearchFragment();

            case R.id.action_following:
                return new FollowingFragment();

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
        } else if (f instanceof MultiFilterFragment) {
            return R.id.action_multi_filter;
        } else { // CategoryFragment.
            return R.id.action_category;
        }
    }
}
