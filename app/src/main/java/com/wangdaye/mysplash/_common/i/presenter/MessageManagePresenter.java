package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;

/**
 * Message mange presenter.
 * */

public interface MessageManagePresenter {

    void sendMessage(int what, Object o);
    void responseMessage(Activity a, int what, Object o);
}
