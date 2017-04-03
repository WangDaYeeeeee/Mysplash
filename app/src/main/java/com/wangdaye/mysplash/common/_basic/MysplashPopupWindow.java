package com.wangdaye.mysplash.common._basic;

import android.content.Context;
import android.widget.PopupWindow;

import com.wangdaye.mysplash.Mysplash;

/**
 * Mysplash popup window.
 *
 * Basic PopupWindow class for Mysplash.
 *
 * */

public class MysplashPopupWindow extends PopupWindow {

    public MysplashPopupWindow(Context context) {
        super(context);
        Mysplash.getInstance().getTopActivity().getPopupList().add(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Mysplash.getInstance()
                        .getTopActivity()
                        .getPopupList()
                        .remove(MysplashPopupWindow.this);
            }
        });
    }
}
