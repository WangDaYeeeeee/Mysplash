package com.wangdaye.mysplash.photo.presenter;

import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.view.MessageManageView;

/**
 * Message manage implementor.
 * */

public class MessageManageImplementor implements MessageManagePresenter {
    // view.
    private MessageManageView view;

    /** <br> life cycle. */

    public MessageManageImplementor(MessageManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void sendMessage(int what, Object o) {
        view.sendMessage(what, o);
    }

    @Override
    public void responseMessage(int what, Object o) {
        view.responseMessage(what, o);
    }
}
