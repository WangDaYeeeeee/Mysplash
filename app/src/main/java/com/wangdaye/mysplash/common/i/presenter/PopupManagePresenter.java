package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;
import android.view.View;

/**
 * Popup manage presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.PopupManageView}.
 *
 * */

public interface PopupManagePresenter {

    void showPopup(Context c, View anchor, String value, int position);
}
