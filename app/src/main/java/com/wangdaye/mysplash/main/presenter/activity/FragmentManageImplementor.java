package com.wangdaye.mysplash.main.presenter.activity;

import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.model.FragmentManageModel;
import com.wangdaye.mysplash._common.i.presenter.FragmentManagePresenter;
import com.wangdaye.mysplash._common.i.view.FragmentManageView;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;
import com.wangdaye.mysplash.main.view.fragment.SearchFragment;

/**
 * Fragment manage implementor.
 * */

public class FragmentManageImplementor
        implements FragmentManagePresenter {
    // model & view.
    private FragmentManageModel model;
    private FragmentManageView view;

    /** <br> life cycle. */

    public FragmentManageImplementor(FragmentManageModel model, FragmentManageView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void addFragment(int code) {
        Fragment f = buildFragmentByCode(code);
        model.addFragmentToList(f);
        view.addFragment(f);
    }

    @Override
    public void popFragment() {
        if (model.getFragmentCount() > 0) {
            model.popFragmentFromList();
            view.popFragment();
        }
    }

    @Override
    public void changeFragment(int code) {
        if (model.getFragmentCount() > 1 || model.getFragmentCode() != code) {
            while (model.getFragmentCount() > 0) {
                popFragment();
            }
            Fragment f = buildFragmentByCode(code);
            model.addFragmentToList(f);
            view.changeFragment(f);
        }
    }

    /** <br> utils. */

    private Fragment buildFragmentByCode(int code) {
        switch (code) {
            case R.id.action_home:
                return new HomeFragment();

            case R.id.action_search:
                return new SearchFragment();

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
