package com.wangdaye.mysplash.common.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.AccessToken;
import com.wangdaye.mysplash.common.data.service.AuthorizeService;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.AnimUtils;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Login activity.
 *
 * This activity is used to login to the Unsplash account.
 *
 * */

public class LoginActivity extends MysplashActivity
    implements SwipeBackCoordinatorLayout.OnSwipeListener,
        AuthorizeService.OnRequestAccessTokenListener {
    // widget
    @BindView(R.id.activity_login_container) CoordinatorLayout container;
    @BindView(R.id.activity_login_statusBar) StatusBarView statusBar;
    @BindView(R.id.activity_login_buttonContainer) LinearLayout buttonContainer;
    @BindView(R.id.activity_login_progressContainer) RelativeLayout progressContainer;

    // data
    private AuthorizeService service;

    @StateRule
    private int state;

    private static final int NORMAL_STATE = 0;
    private static final int AUTH_STATE = 1;
    @IntDef({NORMAL_STATE, AUTH_STATE})
    private @interface StateRule {}

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initData();
            initWidget();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.cancel();
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    protected boolean operateStatusBarBySelf() {
        return false;
    }

    @Override
    public void finishActivity(int dir) {
        finish();
        switch (dir) {
            case SwipeBackCoordinatorLayout.UP_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_top);
                break;

            case SwipeBackCoordinatorLayout.DOWN_DIR:
                overridePendingTransition(0, R.anim.activity_slide_out_bottom);
                break;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null
                && intent.getData() != null
                && !TextUtils.isEmpty(intent.getData().getAuthority())
                && Mysplash.UNSPLASH_LOGIN_CALLBACK.equals(intent.getData().getAuthority())) {
            service.requestAccessToken(
                    Mysplash.getInstance(),
                    intent.getData().getQueryParameter("code"),
                    this);
            setState(AUTH_STATE);
        }
    }

    @Override
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget() {
        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_login_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        ImageButton closeBtn = ButterKnife.findById(this, R.id.activity_login_closeBtn);
        ThemeManager.setImageResource(closeBtn, R.drawable.ic_close_light, R.drawable.ic_close_dark);

        ImageView icon = ButterKnife.findById(this, R.id.activity_login_icon);
        ImageHelper.loadResourceImage(this, icon, R.drawable.ic_launcher);

        DisplayUtils.setTypeface(this, ((TextView) ButterKnife.findById(this, R.id.activity_login_content)));

        Button loginBtn = ButterKnife.findById(this, R.id.activity_login_loginBtn);
        Button joinBtn = ButterKnife.findById(this, R.id.activity_login_joinBtn);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (ThemeManager.getInstance(this).isLightTheme()) {
                loginBtn.setBackgroundResource(R.color.colorTextTitle_light);
                joinBtn.setBackgroundResource(R.color.colorPrimaryDark_light);
            } else {
                loginBtn.setBackgroundResource(R.color.colorTextTitle_dark);
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

    /** <br> data. */

    private void initData() {
        this.service = AuthorizeService.getService();
        this.state = NORMAL_STATE;
    }

    /** <br> interface. */

    // on click listener.

    @OnClick(R.id.activity_login_closeBtn) void close() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @OnClick(R.id.activity_login_loginBtn) void login() {
        IntentHelper.startWebActivity(this, Mysplash.getLoginUrl(this));
    }

    @OnClick(R.id.activity_login_joinBtn) void join() {
        IntentHelper.startWebActivity(this, Mysplash.UNSPLASH_JOIN_URL);
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishActivity(dir);
    }

    // on request access token listener.

    @Override
    public void onRequestAccessTokenSuccess(Call<AccessToken> call, Response<AccessToken> response) {
        if (response.isSuccessful()) {
            AuthManager.getInstance().writeAccessToken(response.body());
            AuthManager.getInstance().requestPersonalProfile();
            IntentHelper.startMainActivity(this);
            finish();
        } else {
            NotificationHelper.showSnackbar(getString(R.string.feedback_request_token_failed));
            setState(NORMAL_STATE);
        }
    }

    @Override
    public void onRequestAccessTokenFailed(Call<AccessToken> call, Throwable t) {
        NotificationHelper.showSnackbar(getString(R.string.feedback_request_token_failed));
        setState(NORMAL_STATE);
    }
}
