package com.wangdaye.mysplash.main.presenter.activity;

import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.view.MessageManageView;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

/**
 * Message manage implementor.
 *
 * A {@link MessageManagePresenter} for {@link MainActivity}.
 *
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
