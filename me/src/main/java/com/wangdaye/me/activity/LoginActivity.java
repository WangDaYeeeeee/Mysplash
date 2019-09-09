package com.wangdaye.me.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.common.network.UrlCollection;
import com.wangdaye.common.utils.helper.RoutingHelper;
import com.wangdaye.component.ComponentFactory;
import com.wangdaye.base.unsplash.AccessToken;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.AuthorizeService;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.common.image.ImageHelper;
import com.wangdaye.common.utils.DisplayUtils;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.utils.AnimUtils;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.ThemeManager;
import com.wangdaye.me.R;
import com.wangdaye.me.R2;
import com.wangdaye.me.di.component.DaggerApplicationComponent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Login activity.
 *
 * This activity is used to login to the Unsplash account.
 *
 * */

@Route(path = LoginActivity.LOGIN_ACTIVITY)
public class LoginActivity extends MysplashActivity
    implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_login_container) CoordinatorLayout container;
    @BindView(R2.id.activity_login_statusBar) StatusBarView statusBar;
    @BindView(R2.id.activity_login_buttonContainer) LinearLayout buttonContainer;
    @BindView(R2.id.activity_login_progressContainer) RelativeLayout progressContainer;

    @OnClick(R2.id.activity_login_closeBtn) void close() {
        finishSelf(true);
    }

    @OnClick(R2.id.activity_login_loginBtn) void login() {
        RoutingHelper.startWebActivity(this, UrlCollection.getLoginUrl(this));
    }

    @OnClick(R2.id.activity_login_joinBtn) void join() {
        RoutingHelper.startWebActivity(this, UrlCollection.UNSPLASH_JOIN_URL);
    }

    @Inject AuthorizeService service;

    @StateRule
    private int state;

    private static final int NORMAL_STATE = 0;
    private static final int AUTH_STATE = 1;
    @IntDef({NORMAL_STATE, AUTH_STATE})
    private @interface StateRule {}

    public static final String LOGIN_ACTIVITY = "/me/LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        DisplayUtils.setNavigationBarStyle(this, false, false);

        initData();
        initWidget();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.cancel();
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
        if (backPressed) {
            overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null
                && intent.getData() != null
                && !TextUtils.isEmpty(intent.getData().getAuthority())
                && UrlCollection.UNSPLASH_LOGIN_CALLBACK.equals(intent.getData().getAuthority())) {
            service.requestAccessToken(
                    this,
                    intent.getData().getQueryParameter("code"),
                    new BaseObserver<AccessToken>() {
                        @Override
                        public void onSucceed(AccessToken accessToken) {
                            AuthManager.getInstance().updateAccessToken(accessToken);
                            AuthManager.getInstance().requestPersonalProfile();
                            ComponentFactory.getMainModule().startMainActivity(LoginActivity.this);
                            finish();
                        }

                        @Override
                        public void onFailed() {
                            NotificationHelper.showSnackbar(
                                    LoginActivity.this,
                                    getString(R.string.feedback_request_token_failed)
                            );
                            setState(NORMAL_STATE);
                        }
                    }
            );
            setState(AUTH_STATE);
        }
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    private void initWidget() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_login_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        AppCompatImageView icon = findViewById(R.id.activity_login_icon);
        ImageHelper.loadResourceImage(this, icon, R.drawable.ic_launcher);

        Button loginBtn = findViewById(R.id.activity_login_loginBtn);
        Button joinBtn = findViewById(R.id.activity_login_joinBtn);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (ThemeManager.getInstance(this).isLightTheme()) {
                loginBtn.setBackgroundResource(R.color.colorPrimaryDark_dark);
                joinBtn.setBackgroundResource(R.color.colorPrimaryDark_light);
            } else {
                loginBtn.setBackgroundResource(R.color.colorPrimaryDark_light);
                joinBtn.setBackgroundResource(R.color.colorPrimaryDark_dark);
            }
        } else {
            loginBtn.setBackgroundResource(R.drawable.button_login);
            joinBtn.setBackgroundResource(R.drawable.button_join);
        }

        progressContainer.setVisibility(View.GONE);
    }

    public void setState(int newState) {
        switch (newState) {
            case NORMAL_STATE:
                if (state == AUTH_STATE) {
                    AnimUtils.animShow(buttonContainer);
                    AnimUtils.animHide(progressContainer);
                }
                break;

            case AUTH_STATE:
                if (state == NORMAL_STATE) {
                    AnimUtils.animShow(progressContainer);
                    AnimUtils.animHide(buttonContainer);
                }
                break;
        }
        state = newState;
    }

    private void initData() {
        this.state = NORMAL_STATE;
    }

    // interface.

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
