package com.wangdaye.mysplash._common.i.presenter;

/**
 * Message mange presenter.
 * */

public interface MessageManagePresenter {

    void sendMessage(int what, Object o);
    void responseMessage(int what, Object o);
}
