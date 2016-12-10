package com.wangdaye.mysplash._common.ui._basic;

import android.content.Context;
import android.widget.PopupWindow;

import com.wangdaye.mysplash.Mysplash;

/**
 * Mysplash popup window.
 * */

public class MysplashPopupWindow extends PopupWindow {

    public MysplashPopupWindow(Context context) {
        super(context);
        Mysplash.getInstance().getTopActivity().getPopupList().add(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Mysplash.getInstance().getTopActivity().getPopupList().remove(MysplashPopupWindow.this);
            }
        });
    }
}
