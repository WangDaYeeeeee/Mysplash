package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.api.PhotoApi;
import com.wangdaye.mysplash.main.model.fragment.i.SearchModel;
import com.wangdaye.mysplash.main.presenter.fragment.i.SearchBarPresenter;
import com.wangdaye.mysplash.main.view.fragment.i.SearchBarView;

/**
 * Search bar implementor.
 * */

public class SearchBarImp
        implements SearchBarPresenter,
        PopupMenu.OnMenuItemClickListener {
    // model.
    private SearchModel searchModel;

    // view.
    private SearchBarView searchBarView;

    /** <br> life cycle. */

    public SearchBarImp(SearchModel searchModel, SearchBarView searchBarView) {
        this.searchModel = searchModel;
        this.searchBarView = searchBarView;
    }

    @Override
    public void clickNavigationIcon() {
        searchBarView.clickNavigationIcon();
    }

    @Override
    public void clickMenuItem(int id) {
        switch (id) {
            case R.id.action_clear_text:
                searchBarView.clearSearchText();
                break;
        }
    }

    @Override
    public void showOrientationMenu(Context c, View anchor) {
        PopupMenu menu = new PopupMenu(c, anchor);
        menu.inflate(R.menu.menu_fragment_search_orientation);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public void clickSearchBar() {
        searchBarView.scrollToTop();
    }

    @Override
    public void inputSearchQuery(String text) {
        if (!text.equals("")) {
            searchBarView.inputSearchQuery(text, searchModel.getOrientation());
        }
        searchBarView.hideKeyboard();
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_orientation_landscape:
                searchModel.setOrientation(PhotoApi.LANDSCAPE_ORIENTATION);
                searchBarView.changeOrientation(searchModel.getOrientation());
                return true;

            case R.id.action_orientation_portrait:
                searchModel.setOrientation(PhotoApi.PORTRAIT_ORIENTATION);
                searchBarView.changeOrientation(searchModel.getOrientation());
                return true;

            case R.id.action_orientation_square:
                searchModel.setOrientation(PhotoApi.SQUARE_ORIENTATION);
                searchBarView.changeOrientation(searchModel.getOrientation());
                return true;
        }
        return false;
    }
}
