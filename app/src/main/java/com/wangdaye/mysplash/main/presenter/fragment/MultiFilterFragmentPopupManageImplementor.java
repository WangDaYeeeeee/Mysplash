package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.popup.SearchCategoryPopupWindow;
import com.wangdaye.mysplash._common.ui.popup.SearchFeaturedPopupWindow;
import com.wangdaye.mysplash._common.ui.popup.SearchOrientationPopupWindow;

/**
 * Multi-filter fragment popup manage implementor.
 * */

public class MultiFilterFragmentPopupManageImplementor
        implements PopupManagePresenter {
    // model & view.
    private PopupManageView view;

    /** <br> life cycle. */

    public MultiFilterFragmentPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void showPopup(Context c, View anchor, String value, final int position) {
        switch (position) {
            case 0:
                SearchCategoryPopupWindow category = new SearchCategoryPopupWindow(
                        c,
                        anchor,
                        Integer.parseInt(value));
                category.setOnSearchCategoryChangedListener(new SearchCategoryPopupWindow.OnSearchCategoryChangedListener() {
                    @Override
                    public void onSearchCategoryChanged(int categoryId) {
                        view.responsePopup(String.valueOf(categoryId), position);
                    }
                });
                break;

            case 1:
                SearchOrientationPopupWindow orientation = new SearchOrientationPopupWindow(
                        c,
                        anchor,
                        value);
                orientation.setOnSearchOrientationChangedListener(new SearchOrientationPopupWindow.OnSearchOrientationChangedListener() {
                    @Override
                    public void onSearchOrientationChanged(String orientationValue) {
                        view.responsePopup(orientationValue, position);
                    }
                });
                break;

            case 2:
                SearchFeaturedPopupWindow featured = new SearchFeaturedPopupWindow(
                        c,
                        anchor,
                        value);
                featured.setOnSearchFeaturedChangedListener(new SearchFeaturedPopupWindow.OnSearchFeaturedChangedListener() {
                    @Override
                    public void onSearchFeaturedChanged(boolean newValue) {
                        view.responsePopup(String.valueOf(newValue), position);
                    }
                });
                break;
        }
    }
}
