package com.wangdaye.mysplash.photo.presenter;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.PopupManageView;
import com.wangdaye.mysplash._common.ui.popup.PhotoMenuPopupWindow;

/**
 * Photo activity popup manage implementor.
 * */

public class PhotoActivityPopupManageImplementor
        implements PopupManagePresenter,
        PhotoMenuPopupWindow.OnSelectItemListener {
    // model & view.
    private PopupManageView view;

    /** <br> life cycle. */

    public PhotoActivityPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void showPopup(Context c, View anchor, String value, int position) {
        PhotoMenuPopupWindow popup = new PhotoMenuPopupWindow(c, anchor);
        popup.setOnSelectItemListener(this);
    }

    @Override
    public void onSelectItem(int id) {
        view.responsePopup(null, id);
    }
}
