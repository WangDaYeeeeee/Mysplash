package com.wangdaye.mysplash.main.presenter.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common._basic.MysplashFragment;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.FollowingFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.main.view.fragment.MultiFilterFragment;
import com.wangdaye.mysplash.main.view.fragment.SearchFragment;

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
    public List<MysplashFragment> getFragmentList() {
        return model.getFragmentList();
    }

    @Override
    public MysplashFragment getTopFragment() {
        return model.getFragmentFromList(model.getFragmentCount() - 1);
    }

    @Override
    public List<Integer> getIdList() {
        return model.getIdList();
    }

    @Override
    public void clearIdList() {
        model.getIdList().clear();
    }

    /** <br> presenter. */

    @Override
    public void changeFragment(MysplashActivity a, Bundle saveInstanceState, int code) {
        if (model.getFragmentCount() > 1) {
            while (model.getFragmentCount() > 1) {
                popFragment(a);
            }
        }

        MysplashFragment f = buildFragmentByCode(saveInstanceState, code);
        model.getFragmentList().clear();
        model.getIdList().clear();
        model.addFragmentToList(f, code);
        a.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
    }

    @Override
    public void addFragment(MysplashActivity a, Bundle saveInstanceState, int code) {
        MysplashFragment f = buildFragmentByCode(saveInstanceState, code);
        model.addFragmentToList(f, code);

        a.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, f)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void popFragment(MysplashActivity a) {
        if (model.getFragmentCount() > 0) {
            model.popFragmentFromList();
            a.getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public int getFragmentCount() {
        return model.getFragmentCount();
    }

    /** <br> utils. */

    private MysplashFragment buildFragmentByCode(Bundle saveInstanceState, int code) {
        switch (code) {
            case R.id.action_home:
                return new HomeFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_search:
                return new SearchFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_following:
                return new FollowingFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_multi_filter:
                return new MultiFilterFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_category:
                return new CategoryFragment()
                        .readBundle(saveInstanceState);

            default:
                return null;
        }
    }
}
