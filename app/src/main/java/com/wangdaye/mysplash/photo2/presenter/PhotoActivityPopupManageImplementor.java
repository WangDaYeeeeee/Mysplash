package com.wangdaye.mysplash.photo2.presenter;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash.photo2.view.activity.PhotoActivity2;

/**
 * Photo activity popup manage implementor.
 * */

public class PhotoActivityPopupManageImplementor
        implements PopupManagePresenter,
        PhotoMenuPopupWindow.OnSelectItemListener {

    private PopupManageView view;

    public PhotoActivityPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    @Override
    public void showPopup(Context c, View anchor, String value, int position) {
        MysplashActivity activity = Mysplash.getInstance().getTopActivity();
        if (activity != null && activity instanceof PhotoActivity2) {
            Photo photo = ((PhotoActivity2) activity).getPhoto();
            if (photo != null) {
                PhotoMenuPopupWindow popup = new PhotoMenuPopupWindow(c, anchor);
                popup.setOnSelectItemListener(this);
            }
        }
    }

    @Override
    public void onSelectItem(int id) {
        view.responsePopup(null, id);
    }
}
