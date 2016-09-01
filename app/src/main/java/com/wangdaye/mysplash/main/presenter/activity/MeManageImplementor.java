package com.wangdaye.mysplash.main.presenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.i.presenter.MeManagePresenter;
import com.wangdaye.mysplash._common.i.view.MeManageView;
import com.wangdaye.mysplash._common.ui.activity.LoginActivity;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash.me.view.activity.MeActivity;

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
    public void touchMeAvatar(Activity a) {
        if (!AuthManager.getInstance().isAuthorized()) {
            Intent intent = new Intent(a, LoginActivity.class);
            a.startActivity(intent);
        } else {
            Intent intent = new Intent(a, MeActivity.class);

            NavigationView nav = (NavigationView) a.findViewById(R.id.activity_main_navView);
            View header = nav.getHeaderView(0);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            a,
                            Pair.create(
                                    header.findViewById(R.id.container_nav_header_avatar),
                                    a.getString(R.string.transition_me_avatar)));
            ActivityCompat.startActivity(a, intent, options.toBundle());
        }
    }

    @Override
    public void touchMeButton(Activity a) {
        if (!AuthManager.getInstance().isAuthorized()) {
            Intent intent = new Intent(a, LoginActivity.class);
            a.startActivity(intent);
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
