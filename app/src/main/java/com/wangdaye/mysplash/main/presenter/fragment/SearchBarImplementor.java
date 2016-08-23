package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash._common.i.model.SearchBarModel;
import com.wangdaye.mysplash._common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash._common.i.view.SearchBarView;

/**
 * Search bar implementor.
 * */

public class SearchBarImplementor
        implements SearchBarPresenter {
    // model & view.
    private SearchBarModel model;
    private SearchBarView view;

    /** <br> life cycle. */

    public SearchBarImplementor(SearchBarModel model, SearchBarView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon() {
        view.touchNavigatorIcon();
    }

    @Override
    public void touchMenuItem(int itemId) {
        view.touchMenuItem(itemId);
    }

    @Override
    public void touchOrientationIcon() {
        view.touchOrientationIcon();
    }

    @Override
    public void touchSearchBar() {
        view.touchSearchBar();
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
    public void setOrientation(String orientation) {
        model.setOrientation(orientation);
    }

    @Override
    public void submitSearchInfo(String text) {
        view.submitSearchInfo(text, model.getOrientation());
    }
}
