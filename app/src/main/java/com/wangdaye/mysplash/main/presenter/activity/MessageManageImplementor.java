package com.wangdaye.mysplash.main.presenter.activity;

import android.app.Activity;
import android.content.Intent;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash._common.i.view.MessageManageView;
import com.wangdaye.mysplash._common.ui.activity.AboutActivity;
import com.wangdaye.mysplash._common.ui.activity.DownloadManageActivity;
import com.wangdaye.mysplash._common.ui.activity.SettingsActivity;
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
    public void responseMessage(final Activity a, int what, Object o) {
        switch (what) {
            case R.id.action_change_theme:
                ((MainActivity) a).changeTheme();
                break;

            case R.id.action_download_manage:
                Intent d = new Intent(a, DownloadManageActivity.class);
                a.startActivity(d);
                a.overridePendingTransition(R.anim.activity_in, 0);
                break;

            case R.id.action_settings:
                Intent s = new Intent(a, SettingsActivity.class);
                a.startActivity(s);
                a.overridePendingTransition(R.anim.activity_in, 0);
                break;

            case R.id.action_about:
                Intent about = new Intent(a, AboutActivity.class);
                a.startActivity(about);
                a.overridePendingTransition(R.anim.activity_in, 0);
                break;

            default:
                ((MainActivity) a).changeFragment(what);
                break;
        }
    }
}
