package com.wangdaye.mysplash.main.presenter.activity;

import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.MessageManageView;

/**
 * Message manage implementor.
 * */

public class MessageManageImplementor
        implements MessageManagePresenter {
    // model & view.
    private MessageManageView view;

    /** <br> life cycle. */

    public MessageManageImplementor(MessageManageView view) {
        this.view = view;
    }

    @Override
    public void sendMessage(int what, Object o) {
        view.sendMessage(what, o);
    }

    @Override
    public void responseMessage(int what, Object o) {
        view.responseMessage(what, o);
    }
}
