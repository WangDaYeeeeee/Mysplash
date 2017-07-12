package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.view.MessageManageView;

/**
 * Message manage implementor.
 *
 * */

public class MessageManageImplementor
        implements MessageManagePresenter {

    private MessageManageView view;

    public MessageManageImplementor(MessageManageView view) {
        this.view = view;
    }

    @Override
    public void sendMessage(int what, Object o) {
        view.sendMessage(what, o);
    }

    @Override
    public void responseMessage(int what, Object o) {
        switch (what) {
            case 1:
                view.responseMessage(what, o);
                break;
        }
    }
}
