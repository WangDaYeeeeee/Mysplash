package com.wangdaye.mysplash.search.presenter.activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash.common.i.view.SearchBarView;

/**
 * Search bar implementor.
 *
 * */

public class SearchBarImplementor
        implements SearchBarPresenter {

    private SearchBarView view;

    public SearchBarImplementor(SearchBarView view) {
        this.view = view;
    }

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        a.finishSelf(true);
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
