package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.popup.CollectionTypePopupWindow;
import com.wangdaye.mysplash.common.ui.popup.PhotoOrderPopupWindow;

/**
 * Popup manage implementor.
 *
 * A {@link PopupManagePresenter} for {@link com.wangdaye.mysplash.main.view.fragment.HomeFragment}.
 *
 * */

public class HomeFragmentPopupManageImplementor
        implements PopupManagePresenter {

    private PopupManageView view;

    public HomeFragmentPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    @Override
    public void showPopup(Context c, View anchor, String value, final int position) {
        if (position < 2) {
            PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                    c,
                    anchor,
                    value,
                    PhotoOrderPopupWindow.NORMAL_TYPE);
            window.setOnPhotoOrderChangedListener(new PhotoOrderPopupWindow.OnPhotoOrderChangedListener() {
                @Override
                public void onPhotoOrderChange(String orderValue) {
                    view.responsePopup(orderValue, position);
                }
            });
        } else {
            CollectionTypePopupWindow window = new CollectionTypePopupWindow(
                    c,
                    anchor,
                    value);
            window.setOnCollectionTypeChangedListener(new CollectionTypePopupWindow.OnCollectionTypeChangedListener() {
                @Override
                public void CollectionTypeChange(String typeValue) {
                    view.responsePopup(typeValue, position);
                }
            });
        }
    }
}
