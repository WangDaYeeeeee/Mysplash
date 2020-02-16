package com.wangdaye.common.base.popup;

import android.view.View;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.utils.DisplayUtils;

/**
 * Mysplash popup window.
 *
 * Basic PopupWindow class for Mysplash.
 *
 * */

public class MysplashPopupWindow {

    public static void show(MysplashActivity activity, View anchor, @MenuRes int resId,
                            @NonNull PopupMenu.OnMenuItemClickListener l) {
        PopupMenu popupMenu = new PopupMenu(activity, anchor);
        popupMenu.getMenuInflater().inflate(resId, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(l);

        // DisplayUtils.setOverflowMenuIconsVisible(popupMenu.getMenu());

        popupMenu.show();
    }
}
