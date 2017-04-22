package com.wangdaye.mysplash.common.i.presenter;

import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Notification bar presenter.
 *
 * A presenter which can show notification button and remind user to check the unread notifications.
 *
 * */

public interface NotificationBarPresenter {

    void setImage(ImageButton bellBtn, ImageView dot);
    void setVisible(ImageButton bellBtn, ImageView dot);
}
