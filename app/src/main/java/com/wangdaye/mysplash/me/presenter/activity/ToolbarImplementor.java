package com.wangdaye.mysplash.me.presenter.activity;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

/**
 * Toolbar implementor.
 * */

public class ToolbarImplementor
        implements ToolbarPresenter {

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        a.finishSelf(true);
    }

    @Override
    public void touchToolbar(MysplashActivity a) {
        // do nothing.
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        switch (itemId) {
            case R.id.action_edit:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    IntentHelper.startUpdateMeActivity(a);
                }
                break;

            case R.id.action_filter:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    ((MeActivity) a).showPopup(true);
                }
                break;

            case R.id.action_menu:
                if (AuthManager.getInstance().isAuthorized()
                        && AuthManager.getInstance().getMe() != null) {
                    ((MeActivity) a).showPopup(false);
                }
                break;
        }
        return true;
    }
}
