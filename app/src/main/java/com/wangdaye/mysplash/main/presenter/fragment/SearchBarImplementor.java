package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash._common.i.view.SearchBarView;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

/**
 * Search bar implementor.
 * */

public class SearchBarImplementor
        implements SearchBarPresenter {
    // models & view.
    private SearchBarView view;

    /** <br> life cycle. */

    public SearchBarImplementor(SearchBarView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        ((MainActivity) a).removeFragment();
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        switch (itemId) {
            case R.id.action_clear_text:
                view.clearSearchBarText();
                break;
        }
        return true;
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
    public void submitSearchInfo(String text) {
        view.submitSearchInfo(text);
    }
}
