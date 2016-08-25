package com.wangdaye.mysplash._common.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.data.AccessToken;
import com.wangdaye.mysplash._common.data.service.AuthorizeService;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.ui.widget.StatusBarView;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.LinkUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Login activity.
 * */

public class LoginActivity extends MysplashActivity
        implements View.OnClickListener, AuthorizeService.OnRequestAccessTokenListener {
    // widget
    private LinearLayout buttonContainer;
    private RelativeLayout progressContainer;

    // data
    private AuthorizeService service;

    private int state;
    private final int NORMAL_STATE = 0;
    private final int AUTH_STATE = 1;

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
            initData();
            initWidget();
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Common);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null
                && intent.getData() != null
                && !TextUtils.isEmpty(intent.getData().getAuthority())
                && Mysplash.UNSPLASH_LOGIN_CALLBACK.equals(intent.getData().getAuthority())) {
            service.requestAccessToken(intent.getData().getQueryParameter("code"), this);
            setState(AUTH_STATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.cancel();
    }

    /** <br> UI. */

    private void initWidget() {
        StatusBarView statusBar = (StatusBarView) findViewById(R.id.activity_login_statusBar);
        if (ThemeUtils.getInstance(this).isNeedSetStatusBarMask()) {
            statusBar.setMask(true);
        }

        ImageButton closeBtn = (ImageButton) findViewById(R.id.activity_login_closeBtn);
        closeBtn.setOnClickListener(this);
        if (ThemeUtils.getInstance(this).isLightTheme()) {
            closeBtn.setImageResource(R.drawable.ic_close_light);
        } else {
            closeBtn.setImageResource(R.drawable.ic_close_dark);
        }

        ImageView icon = (ImageView) findViewById(R.id.activity_login_icon);
        Glide.with(this)
                .load(R.drawable.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(icon);

        TypefaceUtils.setTypeface(this, ((TextView) findViewById(R.id.activity_login_content)));

        Button loginBtn = (Button) findViewById(R.id.activity_login_loginBtn);
        loginBtn.setOnClickListener(this);

        Button joinBtn = (Button) findViewById(R.id.activity_login_joinBtn);
        joinBtn.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loginBtn.setBackgroundResource(R.drawable.button_login);
            joinBtn.setBackgroundResource(R.drawable.button_join);
        }

        this.buttonContainer = (LinearLayout) findViewById(R.id.activity_login_buttonContainer);

        this.progressContainer = (RelativeLayout) findViewById(R.id.activity_login_progressContainer);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_login_closeBtn:
                finish();
                break;

            case R.id.activity_login_loginBtn:
                LinkUtils.accessLink(this, Mysplash.UNSPLASH_LOGIN_URL);
                break;

            case R.id.activity_login_joinBtn:
                LinkUtils.accessLink(this, Mysplash.UNSPLASH_JOIN_URL);
                break;
        }
    }

    // on request access token listener.

    @Override
    public void onRequestAccessTokenSuccess(Call<AccessToken> call, Response<AccessToken> response) {
        if (response.isSuccessful()) {
            AuthManager.getInstance().writeAccessToken(response.body());
            AuthManager.getInstance().refreshPersonalProfile();
            MaterialToast.makeText(
                    this,
                    "Welcome back.",
                    null,
                    MaterialToast.LENGTH_SHORT).show();
            finish();
        } else {
            MaterialToast.makeText(
                    this,
                    getString(R.string.feedback_request_token_failed),
                    null,
                    MaterialToast.LENGTH_SHORT).show();
            setState(NORMAL_STATE);
        }
    }

    @Override
    public void onRequestAccessTokenFailed(Call<AccessToken> call, Throwable t) {
        MaterialToast.makeText(
                this,
                getString(R.string.feedback_request_token_failed),
                null,
                MaterialToast.LENGTH_SHORT).show();
        setState(NORMAL_STATE);
    }
}
