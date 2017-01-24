package com.wangdaye.mysplash.main.presenter.fragment;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.popup.PhotoOrderPopupWindow;

/**
 * Category fragment popup manage implementor.
 * */

public class CategoryFragmentPopupManageImplementor
        implements PopupManagePresenter,
        PhotoOrderPopupWindow.OnPhotoOrderChangedListener {
    // model & view.
    private PopupManageView view;

    /** <br> life cycle. */

    public CategoryFragmentPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void showPopup(Context c, View anchor, String value, int position) {
        PhotoOrderPopupWindow window = new PhotoOrderPopupWindow(
                c,
                anchor,
                value,
                PhotoOrderPopupWindow.CATEGORY_TYPE);
        window.setOnPhotoOrderChangedListener(this);
    }

    @Override
    public void onPhotoOrderChange(String orderValue) {
        view.responsePopup(orderValue, 0);
    }
}