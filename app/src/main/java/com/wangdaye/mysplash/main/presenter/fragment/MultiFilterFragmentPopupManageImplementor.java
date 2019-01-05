package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.popup.SearchFeaturedPopupWindow;
import com.wangdaye.mysplash.common.ui.popup.SearchOrientationPopupWindow;

/**
 * Multi-filter fragment popup manage implementor.
 *
 * */

public class MultiFilterFragmentPopupManageImplementor
        implements PopupManagePresenter {

    private PopupManageView view;

    public MultiFilterFragmentPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    @Override
    public void showPopup(Context c, View anchor, String value, final int position) {
        switch (position) {
            case 0:
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

            case 1:
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
