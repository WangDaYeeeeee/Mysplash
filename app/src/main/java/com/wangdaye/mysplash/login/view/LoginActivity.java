package com.wangdaye.mysplash.login.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.tools.AuthManager;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.LoginModel;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.LoginPresenter;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.LoginView;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.toast.MaterialToast;
import com.wangdaye.mysplash._common.utils.LinkUtils;
import com.wangdaye.mysplash._common.utils.ThemeUtils;
import com.wangdaye.mysplash._common.utils.TypefaceUtils;
import com.wangdaye.mysplash.login.model.LoadObject;
import com.wangdaye.mysplash.login.model.LoginObject;
import com.wangdaye.mysplash.login.presenter.LoadImplementor;
import com.wangdaye.mysplash.login.presenter.LoginImplementor;

/**
 * Login activity.
 * */

public class LoginActivity extends MysplashActivity
        implements LoginView, LoadView,
        View.OnClickListener {
    // model.
    private LoginModel loginModel;
    private LoadModel loadModel;

    // view.
    private LinearLayout buttonContainer;
    private RelativeLayout progressContainer;

    // presenter.
    private LoginPresenter loginPresenter;
    private LoadPresenter loadPresenter;

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
            initModel();
            initView();
            initPresenter();
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
        loginPresenter.checkAuthCallback(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginPresenter.cancelRequest();
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.loginPresenter = new LoginImplementor(loginModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
    }

    /** <br> view. */

    private void initView() {
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

        findViewById(R.id.activity_login_loginBtn).setOnClickListener(this);
        findViewById(R.id.activity_login_joinBtn).setOnClickListener(this);

        this.buttonContainer = (LinearLayout) findViewById(R.id.activity_login_buttonContainer);

        this.progressContainer = (RelativeLayout) findViewById(R.id.activity_login_progressContainer);
        progressContainer.setVisibility(View.GONE);
    }

    /** <br> model. */

    private void initModel() {
        this.loginModel = new LoginObject();
        this.loadModel = new LoadObject(LoadObject.NORMAL_STATE);
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
                loginPresenter.showAuthorizeView(this);
                break;

            case R.id.activity_login_joinBtn:
                LinkUtils.accessLink(this, Mysplash.UNSPLASH_JOIN_URL);
                break;
        }
    }

    // view.

    // login view.

    @Override
    public void onAuthCallback() {
        loadPresenter.setLoadingState();
    }

    @Override
    public void requestAccessTokenSuccess() {
        AuthManager.getInstance().refreshPersonalProfile();
        MaterialToast.makeText(
                this,
                "Welcome back.",
                null,
                MaterialToast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void requestAccessTokenFailed() {
        loadPresenter.setNormalState();
    }

    // load view.

    @Override
    public void animShow(final View v) {
        if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

    @Override
    public void animHide(final View v) {
        ObjectAnimator anim = ObjectAnimator
                .ofFloat(v, "alpha", 1, 0)
                .setDuration(300);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    @Override
    public void setLoadingState() {
        animShow(progressContainer);
        animHide(buttonContainer);
    }

    @Override
    public void setFailedState() {
        // do nothing.
    }

    @Override
    public void setNormalState() {
        animShow(buttonContainer);
        animHide(progressContainer);
    }

    @Override
    public void resetLoadingState() {
        // do nothing.
    }
}
