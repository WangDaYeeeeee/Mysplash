package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.main.model.fragment.i.CategoryModel;
import com.wangdaye.mysplash.main.presenter.fragment.i.CategoryMenuPresenter;
import com.wangdaye.mysplash.main.view.fragment.CategoryFragment;
import com.wangdaye.mysplash.main.view.fragment.i.FragmentView;

/**
 * Category menu implementor.
 * */

public class CategoryMenuImp
        implements CategoryMenuPresenter {
    // data.
    private CategoryModel categoryModel;

    // view.
    private FragmentView fragmentView;

    /** <br> life cycle. */

    public CategoryMenuImp(CategoryModel categoryModel, FragmentView fragmentView) {
        this.categoryModel = categoryModel;
        this.fragmentView = fragmentView;
    }

    /** <br> presenter. */

    @Override
    public void clickRandomItem(Context c) {
        saveMode(c, false);
        CategoryFragment f = new CategoryFragment();
        f.initModel(c, categoryModel.getCategoryId());
        fragmentView.changeFragment(f);
    }

    @Override
    public void clickNormalItem(Context c) {
        saveMode(c, true);
        CategoryFragment f = new CategoryFragment();
        f.initModel(c, categoryModel.getCategoryId());
        fragmentView.changeFragment(f);
    }

    private void saveMode(Context c, boolean b) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
        editor.putBoolean(
                c.getString(R.string.key_normal_mode),
                b);
        editor.apply();
    }
}
