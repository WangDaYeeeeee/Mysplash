package com.wangdaye.mysplash.main.presenter.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.i.presenter.FragmentManagePresenter;
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
    public List<Fragment> getFragmentList() {
        return model.getFragmentList();
    }

    @Override
    public Fragment getTopFragment() {
        return model.getFragmentFromList(model.getFragmentCount() - 1);
    }

    /** <br> presenter. */

    @Override
    public void addFragment(Activity a, int code) {
        Fragment f = buildFragmentByCode(code);
        model.addFragmentToList(f);

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
    public void changeFragment(Activity a, int code) {
        if (model.getFragmentCount() > 1) {
            while (model.getFragmentCount() > 1) {
                popFragment(a);
            }
        }

        Fragment f = buildFragmentByCode(code);
        model.getFragmentList().clear();
        model.addFragmentToList(f);
        ((MainActivity) a).getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_main_fragment, f)
                .commit();
    }

    /** <br> utils. */

    private Fragment buildFragmentByCode(int code) {
        switch (code) {
            case R.id.action_home:
                return new HomeFragment();

            case R.id.action_search:
                return new SearchFragment();

            case R.id.action_multi_filter:
                return new MultiFilterFragment();

            case R.id.action_category_buildings:
                return new CategoryFragment().setCategory(Mysplash.CATEGORY_BUILDINGS_ID);

            case R.id.action_category_food_drink:
                return new CategoryFragment().setCategory(Mysplash.CATEGORY_FOOD_DRINK_ID);

            case R.id.action_category_nature:
                return new CategoryFragment().setCategory(Mysplash.CATEGORY_NATURE_ID);

            case R.id.action_category_objects:
                return new CategoryFragment().setCategory(Mysplash.CATEGORY_OBJECTS_ID);

            case R.id.action_category_people:
                return new CategoryFragment().setCategory(Mysplash.CATEGORY_PEOPLE_ID);

            case R.id.action_category_technology:
                return new CategoryFragment().setCategory(Mysplash.CATEGORY_TECHNOLOGY_ID);

            default:
                return null;
        }
    }
}
