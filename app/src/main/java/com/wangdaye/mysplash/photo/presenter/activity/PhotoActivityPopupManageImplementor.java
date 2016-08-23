package com.wangdaye.mysplash.photo.presenter.activity;

import android.content.Context;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash._common.i.view.PopupManageView;

/**
 * Photo activity popup manage implementor.
 * */

public class PhotoActivityPopupManageImplementor
        implements PopupManagePresenter,
        PopupMenu.OnMenuItemClickListener {
    // model & view.
    private PopupManageView view;

    /** <br> life cycle. */

    public PhotoActivityPopupManageImplementor(PopupManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void showPopup(Context c, View anchor, String value, int position) {
        PopupMenu menu = new PopupMenu(c, anchor, Gravity.CENTER);
        menu.inflate(R.menu.activity_photo);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        view.responsePopup(null, menuItem.getItemId());
        return true;
    }
}
