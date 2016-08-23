package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;
import android.view.View;

/**
 * Popup manage presenter.
 * */

public interface PopupManagePresenter {

    void showPopup(Context c, View anchor, String value, int position);
}
