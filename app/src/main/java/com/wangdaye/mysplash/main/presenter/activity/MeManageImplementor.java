package com.wangdaye.mysplash.main.presenter.activity;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.i.presenter.MeManagePresenter;
import com.wangdaye.mysplash._common.i.view.MeManageView;
import com.wangdaye.mysplash._common.utils.NotificationUtils;

/**
 * Me manage implementor.
 * */

public class MeManageImplementor
        implements MeManagePresenter {
    // model & view.
    private MeManageView view;

    /** <br> life cycle. */

    public MeManageImplementor(MeManageView view) {
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void touchMeAvatar(MysplashActivity a) {
        NavigationView nav = (NavigationView) a.findViewById(R.id.activity_main_navView);
        View header = nav.getHeaderView(0);
        IntentHelper.startMeActivity(
                a,
                header.findViewById(R.id.container_nav_header_avatar));
    }

    @Override
    public void touchMeButton(MysplashActivity a) {
        if (!AuthManager.getInstance().isAuthorized()) {
            IntentHelper.startLoginActivity(a);
        } else {
            AuthManager.getInstance().logout();
        }
    }

    @Override
    public void responseWriteAccessToken() {
        NotificationUtils.showSnackbar(
                "Welcome back.",
                Snackbar.LENGTH_SHORT);
        view.drawMeAvatar();
        view.drawMeTitle();
        view.drawMeSubtitle();
        view.drawMeButton();
    }

    @Override
    public void responseWriteUserInfo() {
        view.drawMeTitle();
        view.drawMeSubtitle();
    }

    @Override
    public void responseWriteAvatarPath() {
        view.drawMeAvatar();
    }

    @Override
    public void responseLogout() {
        view.drawMeAvatar();
        view.drawMeTitle();
        view.drawMeSubtitle();
        view.drawMeButton();
    }
}
