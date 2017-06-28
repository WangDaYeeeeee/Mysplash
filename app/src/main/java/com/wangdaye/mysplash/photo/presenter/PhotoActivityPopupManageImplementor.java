package com.wangdaye.mysplash.photo.presenter;

import android.content.Context;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common.ui.popup.PhotoMenuPopupWindow;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

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
        if (activity != null && activity instanceof PhotoActivity) {
            Photo photo = ((PhotoActivity) activity).getPhoto();
            if (photo != null) {
                PhotoMenuPopupWindow popup = new PhotoMenuPopupWindow(c, anchor, photo);
                popup.setOnSelectItemListener(this);
            }
        }
    }

    @Override
    public void onSelectItem(int id) {
        view.responsePopup(null, id);
    }
}
