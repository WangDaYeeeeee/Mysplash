package com.wangdaye.mysplash.main.presenter.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.ModeUtils;
import com.wangdaye.mysplash.common.view.activity.AboutActivity;
import com.wangdaye.mysplash.common.view.activity.SettingsActivity;
import com.wangdaye.mysplash.main.model.activity.DrawerObject;
import com.wangdaye.mysplash.main.model.activity.i.DrawerModel;
import com.wangdaye.mysplash.main.presenter.activity.i.DrawerPresenter;
import com.wangdaye.mysplash.main.view.activity.i.DrawerView;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.HomeFragment;

/**
 * Drawer implementor.
 * */

public class DrawerImp
        implements DrawerPresenter {
    // model.
    private DrawerModel drawerModel;

    // view.
    private DrawerView drawerView;

    /** <br> life cycle. */

    public DrawerImp(DrawerModel fmModel, DrawerView fmView) {
        drawerModel = fmModel;
        drawerView = fmView;
    }

    /** <br> presenter. */

    // drawer.

    @Override
    public void selectDrawerItem(int id) {
        if (id == drawerModel.getMenuItemId()) {
            return;
        }
        if (id != R.id.action_change_theme
                && id != R.id.action_settings
                && id != R.id.action_about
                && id != R.id.action_null) {
            drawerModel.setMenuItemId(id);
        }
        sendMessage(id);
        drawerView.closeDrawer();
    }

    private void sendMessage(int itemId) {
        switch (itemId) {
            case R.id.action_home:
                drawerView.sendMessage(DrawerObject.HOME_FRAGMENT);
                break;

            case R.id.action_category_buildings:
                drawerView.sendMessage(Mysplash.CATEGORY_BUILDINGS_ID);
                break;

            case R.id.action_category_food_drink:
                drawerView.sendMessage(Mysplash.CATEGORY_FOOD_DRINK_ID);
                break;

            case R.id.action_category_nature:
                drawerView.sendMessage(Mysplash.CATEGORY_NATURE_ID);
                break;

            case R.id.action_category_objects:
                drawerView.sendMessage(Mysplash.CATEGORY_OBJECTS_ID);
                break;

            case R.id.action_category_people:
                drawerView.sendMessage(Mysplash.CATEGORY_PEOPLE_ID);
                break;

            case R.id.action_category_technology:
                drawerView.sendMessage(Mysplash.CATEGORY_TECHNOLOGY_ID);
                break;

            case R.id.action_change_theme:
                drawerView.sendMessage(DrawerObject.CHANGE_THEME);
                break;

            case R.id.action_settings:
                drawerView.sendMessage(DrawerObject.SETTINGS_ACTIVITY);
                break;

            case R.id.action_about:
                drawerView.sendMessage(DrawerObject.ABOUT_ACTIVITY);
                break;
        }
    }

    // manage.

    @Override
    public void changeFragment(Fragment f) {
        if (drawerModel.getFragmentCount() > 0) {
            clearFragment();
        }
        drawerModel.addFragmentToList(f);
        drawerView.changeFragment(f);
    }

    @Override
    public void addFragment(Fragment f) {
        drawerModel.addFragmentToList(f);
        drawerView.addFragment(f);
    }

    @Override
    public void removeFragment() {
        drawerModel.removeFragmentFromList();
        drawerView.removeFragment();
    }

    @Override
    public void clearFragment() {
        while (drawerModel.getFragmentCount() > 0) {
            removeFragment();
        }
    }

    // build.

    @Override
    public void processMessage(Context c, int what) {
        switch (what) {
            case DrawerObject.HOME_FRAGMENT:
                showHomeFragment();
                break;

            case DrawerObject.CHANGE_THEME:
                changeTheme(c);
                break;

            case DrawerObject.SETTINGS_ACTIVITY:
                showSettingsActivity(c);
                break;

            case DrawerObject.ABOUT_ACTIVITY:
                showAboutActivity(c);
                break;

            default:
                showCategoryFragment(what);
                break;
        }
    }

    private void showHomeFragment() {
        HomeFragment f = new HomeFragment();
        changeFragment(f);
    }

    private void showCategoryFragment(int categoryId) {
        CategoryFragment f = new CategoryFragment();
        f.initModel(categoryId);
        changeFragment(f);
    }

    private void changeTheme(Context c) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putBoolean(
                c.getString(R.string.key_light_theme),
                !ModeUtils.getInstance(c).isLightTheme());
        editor.apply();
        ModeUtils.getInstance(c).refresh(c);
        drawerView.reboot();
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
