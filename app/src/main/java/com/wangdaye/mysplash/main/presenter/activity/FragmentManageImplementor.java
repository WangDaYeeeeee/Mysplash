package com.wangdaye.mysplash.main.presenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash._common.ui.fragment.SaveInstanceFragment;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
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
    public List<SaveInstanceFragment> getFragmentList() {
        return model.getFragmentList();
    }

    @Override
    public SaveInstanceFragment getTopFragment() {
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
    public void changeFragment(Activity a, Bundle saveInstanceState, int code) {
        if (model.getFragmentCount() > 1) {
            while (model.getFragmentCount() > 1) {
                popFragment(a);
            }
        }

        SaveInstanceFragment f = buildFragmentByCode(saveInstanceState, code);
        model.getFragmentList().clear();
        model.getIdList().clear();
        model.addFragmentToList(f, code);
        ((MainActivity) a).getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
    }

    @Override
    public void addFragment(Activity a, Bundle saveInstanceState, int code) {
        SaveInstanceFragment f = buildFragmentByCode(saveInstanceState, code);
        model.addFragmentToList(f, code);

        ((MainActivity) a).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_main_fragment, f)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void popFragment(Activity a) {
        if (model.getFragmentCount() > 0) {
            model.popFragmentFromList();
            ((MainActivity) a).getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public int getFragmentCount() {
        return model.getFragmentCount();
    }

    /** <br> utils. */

    private SaveInstanceFragment buildFragmentByCode(Bundle saveInstanceState, int code) {
        switch (code) {
            case R.id.action_home:
                return new HomeFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_search:
                return new SearchFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_multi_filter:
                return new MultiFilterFragment()
                        .readBundle(saveInstanceState);

            case R.id.action_category_buildings:
                return new CategoryFragment()
                        .setCategory(Mysplash.CATEGORY_BUILDINGS_ID)
                        .readBundle(saveInstanceState);

            case R.id.action_category_food_drink:
                return new CategoryFragment()
                        .setCategory(Mysplash.CATEGORY_FOOD_DRINK_ID)
                        .readBundle(saveInstanceState);

            case R.id.action_category_nature:
                return new CategoryFragment()
                        .setCategory(Mysplash.CATEGORY_NATURE_ID)
                        .readBundle(saveInstanceState);

            case R.id.action_category_objects:
                return new CategoryFragment()
                        .setCategory(Mysplash.CATEGORY_OBJECTS_ID)
                        .readBundle(saveInstanceState);

            case R.id.action_category_people:
                return new CategoryFragment()
                        .setCategory(Mysplash.CATEGORY_PEOPLE_ID)
                        .readBundle(saveInstanceState);

            case R.id.action_category_technology:
                return new CategoryFragment()
                        .setCategory(Mysplash.CATEGORY_TECHNOLOGY_ID)
                        .readBundle(saveInstanceState);

            default:
                return null;
        }
    }
}
