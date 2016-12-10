package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash._common.i.model.MultiFilterBarModel;
import com.wangdaye.mysplash._common.i.presenter.MultiFilterBarPresenter;
import com.wangdaye.mysplash._common.i.view.MultiFilterBarView;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.fragment.MultiFilterFragment;

/**
 * Multi-filter bar implementor.
 * */

public class MultiFilterBarImplementor
        implements MultiFilterBarPresenter {
    // model & view.
    private MultiFilterBarModel model;
    private MultiFilterBarView view;

    /** <br> life cycle. */

    public MultiFilterBarImplementor(MultiFilterBarModel model, MultiFilterBarView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon() {
        view.touchNavigationIcon();
    }

    @Override
    public void touchToolbar(MysplashActivity a) {
        MainActivity activity = (MainActivity) a;
        MultiFilterFragment f = (MultiFilterFragment) activity.getTopFragment();
        f.backToTop();
    }

    @Override
    public void touchSearchButton() {
        view.touchSearchButton();
    }

    @Override
    public void touchMenuContainer(int position) {
        view.touchMenuContainer(position);
    }

    @Override
    public void showKeyboard() {
        view.showKeyboard();
    }

    @Override
    public void hideKeyboard() {
        view.hideKeyboard();
    }

    @Override
    public void submitSearchInfo() {
        view.submitSearchInfo();
    }

    @Override
    public void setQuery(String query) {
        model.setQuery(query);
    }

    @Override
    public String getQuery() {
        return model.getQuery();
    }

    @Override
    public void setUsername(String username) {
        model.setUsername(username);
    }

    @Override
    public String getUsername() {
        return model.getUsername();
    }

    @Override
    public void setCategory(int c) {
        model.setCategory(c);
    }

    @Override
    public int getCategory() {
        return model.getCategory();
    }

    @Override
    public void setOrientation(String o) {
        model.setOrientation(o);
    }

    @Override
    public String getOrientation() {
        return model.getOrientation();
    }

    @Override
    public void setFeatured(boolean f) {
        model.setFeatured(f);
    }

    @Override
    public boolean isFeatured() {
        return model.isFeatured();
    }
}
