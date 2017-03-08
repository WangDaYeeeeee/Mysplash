package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;

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
    public void responseMessage(final MysplashActivity a, int what, Object o) {
        switch (what) {
            case 1:
                view.responseMessage(what, o);
                break;
        }
    }
}
