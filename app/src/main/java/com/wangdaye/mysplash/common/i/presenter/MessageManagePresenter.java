package com.wangdaye.mysplash.common.i.presenter;

/**
 * Message mange presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.MessageManageView}.
 *
 * */

public interface MessageManagePresenter {

    void sendMessage(int what, Object o);
    void responseMessage(int what, Object o);
}
