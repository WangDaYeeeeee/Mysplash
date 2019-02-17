package com.wangdaye.mysplash.main.presenter.activity;

import com.google.android.material.navigation.NavigationView;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.i.presenter.MeManagePresenter;
import com.wangdaye.mysplash.common.i.view.MeManageView;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

/**
 * Me manage implementor.
 *
 * */

public class MeManageImplementor
        implements MeManagePresenter {

    private MeManageView view;

    public MeManageImplementor(MeManageView view) {
        this.view = view;
    }

    @Override
    public void touchMeAvatar(MysplashActivity a) {
        NavigationView nav = a.findViewById(R.id.activity_main_navView);
        View header = nav.getHeaderView(0);
        IntentHelper.startMeActivity(
                a,
                header.findViewById(R.id.container_nav_header_avatar),
                header.findViewById(R.id.container_nav_header),
                UserActivity.PAGE_PHOTO);
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
        NotificationHelper.showSnackbar("Welcome back.");
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
