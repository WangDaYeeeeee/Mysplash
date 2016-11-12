package com.wangdaye.mysplash.main.presenter.activity;

import android.app.Activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

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
    public void responseMessage(Activity a, int what, Object o) {
        switch (what) {
            case R.id.action_change_theme:
                ((MainActivity) a).changeTheme();
                break;

            case R.id.action_download_manage:
                IntentHelper.startDownloadManageActivity(a);
                break;

            case R.id.action_settings:
                IntentHelper.startSettingsActivity(a);
                break;

            case R.id.action_about:
                IntentHelper.startAboutActivity(a);
                break;

            default:
                ((MainActivity) a).changeFragment(what);
                break;
        }
    }
}
