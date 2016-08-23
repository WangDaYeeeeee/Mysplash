package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.popup.CollectionTypePopupWindow;
import com.wangdaye.mysplash._common.ui.popup.PhotoOrderPopupWindow;

/**
 * Popup manage implementor.
 * */

public class HomeFragmentPopupManageImplementor
        implements PopupManagePresenter {
    // model & view.
    private PopupManageView view;

    /** <br> life cycle. */

    public HomeFragmentPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

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
