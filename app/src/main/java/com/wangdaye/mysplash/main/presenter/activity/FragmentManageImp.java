package com.wangdaye.mysplash.main.presenter.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.view.activity.AboutActivity;
import com.wangdaye.mysplash.common.view.activity.SettingsActivity;
import com.wangdaye.mysplash.main.model.activity.FragmentManageObject;
import com.wangdaye.mysplash.main.model.activity.i.FragmentManageModel;
import com.wangdaye.mysplash.main.presenter.activity.i.FragmentManagePresenter;
import com.wangdaye.mysplash.main.view.activity.i.FragmentManageView;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;

/**
 * Fragment manage implementor.
 * */

public class FragmentManageImp
        implements FragmentManagePresenter {
    // model.
    private FragmentManageModel fragmentManageModel;

    // view.
    private FragmentManageView fragmentManageView;

    /** <br> life cycle. */

    public FragmentManageImp(FragmentManageModel fmModel, FragmentManageView fmView) {
        fragmentManageModel = fmModel;
        fragmentManageView = fmView;
    }

    /** <br> presenter. */

    // drawer.

    @Override
    public void selectDrawerItem(int id) {
        if (id == fragmentManageModel.getMenuItemId()) {
            return;
        }
        if (id != R.id.action_settings && id != R.id.action_about) {
            fragmentManageModel.setMenuItemId(id);
        }
        sendMessage(id);
        fragmentManageView.closeDrawer();
    }

    private void sendMessage(int itemId) {
        switch (itemId) {
            case R.id.action_home:
                fragmentManageView.sendMessage(FragmentManageObject.HOME_FRAGMENT);
                break;

            case R.id.action_category_buildings:
                fragmentManageView.sendMessage(Mysplash.CATEGORY_BUILDINGS_ID);
                break;

            case R.id.action_category_food_drink:
                fragmentManageView.sendMessage(Mysplash.CATEGORY_FOOD_DRINK_ID);
                break;

            case R.id.action_category_nature:
                fragmentManageView.sendMessage(Mysplash.CATEGORY_NATURE_ID);
                break;

            case R.id.action_category_objects:
                fragmentManageView.sendMessage(Mysplash.CATEGORY_OBJECTS_ID);
                break;

            case R.id.action_category_people:
                fragmentManageView.sendMessage(Mysplash.CATEGORY_PEOPLE_ID);
                break;

            case R.id.action_category_technology:
                fragmentManageView.sendMessage(Mysplash.CATEGORY_TECHNOLOGY_ID);
                break;

            case R.id.action_settings:
                fragmentManageView.sendMessage(FragmentManageObject.SETTINGS_ACTIVITY);
                break;

            case R.id.action_about:
                fragmentManageView.sendMessage(FragmentManageObject.ABOUT_ACTIVITY);
                break;
        }
    }

    // manage.

    @Override
    public void changeFragment(Fragment f) {
        if (fragmentManageModel.getFragmentCount() > 0) {
            clearFragment();
        }
        fragmentManageModel.addFragmentToList(f);
        fragmentManageView.changeFragment(f);
    }

    @Override
    public void addFragment(Fragment f) {
        fragmentManageModel.addFragmentToList(f);
        fragmentManageView.addFragment(f);
    }

    @Override
    public void removeFragment() {
        fragmentManageModel.removeFragmentFromList();
        fragmentManageView.removeFragment();
    }

    @Override
    public void clearFragment() {
        while (fragmentManageModel.getFragmentCount() > 0) {
            removeFragment();
        }
    }

    // build.

    @Override
    public void processMessage(Context c, int what) {
        switch (what) {
            case FragmentManageObject.HOME_FRAGMENT:
                showHomeFragment();
                break;

            case FragmentManageObject.SETTINGS_ACTIVITY:
                showSettingsActivity(c);
                break;

            case FragmentManageObject.ABOUT_ACTIVITY:
                showAboutActivity(c);
                break;

            default:
                showCategoryFragment(c, what);
                break;
        }
    }

    private void showHomeFragment() {
        HomeFragment f = new HomeFragment();
        changeFragment(f);
    }

    private void showCategoryFragment(Context c, int categoryId) {
        CategoryFragment f = new CategoryFragment();
        f.initModel(c, categoryId);
        changeFragment(f);
    }

    private void showSettingsActivity(Context c) {
        Intent intent = new Intent(c, SettingsActivity.class);
        c.startActivity(intent);
    }

    private void showAboutActivity(Context c) {
        Intent intent = new Intent(c, AboutActivity.class);
        c.startActivity(intent);
    }
}
