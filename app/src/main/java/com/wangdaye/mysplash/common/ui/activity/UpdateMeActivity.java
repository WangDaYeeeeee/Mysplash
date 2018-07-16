package com.wangdaye.mysplash.common.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Me;
import com.wangdaye.mysplash.common.data.service.UserService;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash.common.utils.manager.ShortcutsManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Update me activity.
 *
 * This activity is used to update {@link Me}.
 *
 * */

public class UpdateMeActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener,
        UserService.OnRequestMeProfileListener, SafeHandler.HandlerContainer {

    @BindView(R.id.activity_update_me_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_update_me_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_update_me_scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.container_update_me_progressView)
    CircularProgressView progressView;

    @BindView(R.id.container_update_me_textContainer)
    LinearLayout contentView;

    private EditText usernameTxt;
    private EditText firstNameTxt;
    private EditText lastNameTxt;
    private EditText emailTxt;
    private EditText portfolioTxt;
    private EditText locationTxt;
    private EditText bioTxt;

    private SafeHandler<UpdateMeActivity> handler;

    private UserService service;

    private boolean backPressed = false;

    @StateRule
    private int state;

    private final static int INPUT_STATE = 0;
    private final static int UPDATE_STATE = 1;
    @IntDef({INPUT_STATE, UPDATE_STATE})
    private @interface StateRule {}

    private final String KEY_UPDATE_ME_ACTIVITY_USERNAME = "update_me_activity_username";
    private final String KEY_UPDATE_ME_ACTIVITY_FIRSTNAME = "update_me_activity_firstname";
    private final String KEY_UPDATE_ME_ACTIVITY_LASTNAME = "update_me_activity_lastname";
    private final String KEY_UPDATE_ME_ACTIVITY_EMAIL = "update_me_activity_email";
    private final String KEY_UPDATE_ME_ACTIVITY_PORTFOLIO = "update_me_activity_portfolio";
    private final String KEY_UPDATE_ME_ACTIVITY_LOCATION = "update_me_activity_location";
    private final String KEY_UPDATE_ME_ACTIVITY_BIO = "update_me_activity_bio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_me);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_UPDATE_ME_ACTIVITY_USERNAME, usernameTxt.getText().toString());
        outState.putString(KEY_UPDATE_ME_ACTIVITY_FIRSTNAME, firstNameTxt.getText().toString());
        outState.putString(KEY_UPDATE_ME_ACTIVITY_LASTNAME, lastNameTxt.getText().toString());
        outState.putString(KEY_UPDATE_ME_ACTIVITY_EMAIL, emailTxt.getText().toString());
        outState.putString(KEY_UPDATE_ME_ACTIVITY_PORTFOLIO, portfolioTxt.getText().toString());
        outState.putString(KEY_UPDATE_ME_ACTIVITY_LOCATION, locationTxt.getText().toString());
        outState.putString(KEY_UPDATE_ME_ACTIVITY_BIO, bioTxt.getText().toString());
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
    public void handleBackPressed() {
        if (state == INPUT_STATE && backPressed) {
            finishSelf(true);
        } else if (state == INPUT_STATE) {
            backPressed = true;
            NotificationHelper.showSnackbar(getString(R.string.feedback_click_again_to_exit));

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 2000);
        }
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.service = UserService.getService();
        this.state = INPUT_STATE;
    }

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_update_me_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        ImageButton closeBtn = ButterKnife.findById(this, R.id.container_update_me_closeBtn);
        ThemeManager.setImageResource(closeBtn, R.drawable.ic_close_light, R.drawable.ic_close_dark);

        progressView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);

        this.usernameTxt = ButterKnife.findById(this, R.id.container_update_me_usernameTxt);
        DisplayUtils.setTypeface(this, usernameTxt);

        this.firstNameTxt = ButterKnife.findById(this, R.id.container_update_me_firstNameTxt);
        DisplayUtils.setTypeface(this, firstNameTxt);

        this.lastNameTxt = ButterKnife.findById(this, R.id.container_update_me_lastNameTxt);
        DisplayUtils.setTypeface(this, lastNameTxt);

        this.emailTxt = ButterKnife.findById(this, R.id.container_update_me_emailTxt);
        DisplayUtils.setTypeface(this, emailTxt);

        this.portfolioTxt = ButterKnife.findById(this, R.id.container_update_me_portfolioTxt);
        DisplayUtils.setTypeface(this, portfolioTxt);

        this.locationTxt = ButterKnife.findById(this, R.id.container_update_me_locationTxt);
        DisplayUtils.setTypeface(this, locationTxt);

        this.bioTxt = ButterKnife.findById(this, R.id.container_update_me_bioTxt);
        DisplayUtils.setTypeface(this, bioTxt);

        Button saveBtn = ButterKnife.findById(this, R.id.container_update_me_saveBtn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveBtn.setBackgroundResource(R.drawable.button_login);
        }

        if (getBundle() == null) {
            usernameTxt.setText(AuthManager.getInstance().getMe().username);
            firstNameTxt.setText(AuthManager.getInstance().getMe().first_name);
            lastNameTxt.setText(AuthManager.getInstance().getMe().last_name);
            emailTxt.setText(AuthManager.getInstance().getMe().email);
            portfolioTxt.setText(AuthManager.getInstance().getMe().portfolio_url);
            locationTxt.setText(AuthManager.getInstance().getMe().location);
            bioTxt.setText(AuthManager.getInstance().getMe().bio);
        } else {
            usernameTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_USERNAME,
                            AuthManager.getInstance().getMe().username));
            firstNameTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_FIRSTNAME,
                            AuthManager.getInstance().getMe().first_name));
            lastNameTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_LASTNAME,
                            AuthManager.getInstance().getMe().last_name));
            emailTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_EMAIL,
                            AuthManager.getInstance().getMe().email));
            portfolioTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_PORTFOLIO,
                            AuthManager.getInstance().getMe().portfolio_url));
            locationTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_LOCATION,
                            AuthManager.getInstance().getMe().location));
            bioTxt.setText(
                    getBundle().getString(
                            KEY_UPDATE_ME_ACTIVITY_BIO,
                            AuthManager.getInstance().getMe().bio));
        }
    }

    // control.

    private void setState(int newState) {
        switch (newState) {
            case INPUT_STATE:
                if (state == UPDATE_STATE) {
                    animShow(contentView);
                    animHide(progressView);
                }
                break;

            case UPDATE_STATE:
                if (state == INPUT_STATE) {
                    animShow(progressView);
                    animHide(contentView);
                }
                break;
        }
        state = newState;
    }

    private void updateProfile() {
        String username = usernameTxt.getText().toString();
        if (!TextUtils.isEmpty(username)) {
            service.updateMeProfile(
                    username,
                    firstNameTxt.getText().toString(),
                    lastNameTxt.getText().toString(),
                    emailTxt.getText().toString(),
                    portfolioTxt.getText().toString(),
                    locationTxt.getText().toString(),
                    bioTxt.getText().toString(),
                    this);
            setState(UPDATE_STATE);
        } else {
            NotificationHelper.showSnackbar(getString(R.string.feedback_name_is_required));
        }
    }

    public void animShow(final View v) {
        if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        }
        ObjectAnimator
                .ofFloat(v, "alpha", 0, 1)
                .setDuration(300)
                .start();
    }

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

    // interface.

    // on click listener.

    @OnClick(R.id.container_update_me_closeBtn) void close() {
        finishSelf(true);
    }

    @OnClick(R.id.container_update_me_saveBtn) void save() {
        updateProfile();
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(scrollView, dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        finishSelf(false);
    }

    // on request me profile listener.

    @Override
    public void onRequestMeProfileSuccess(Call<Me> call, Response<Me> response) {
        if (response.isSuccessful() && response.body() != null) {
            AuthManager.getInstance().writeUserInfo(response.body());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutsManager.refreshShortcuts(this);
            }
            finishSelf(true);
        } else {
            setState(INPUT_STATE);
            NotificationHelper.showSnackbar(getString(R.string.feedback_update_profile_failed));
        }
    }

    @Override
    public void onRequestMeProfileFailed(Call<Me> call, Throwable t) {
        setState(INPUT_STATE);
        NotificationHelper.showSnackbar(getString(R.string.feedback_update_profile_failed));
    }

    // handler.

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                backPressed = false;
                break;
        }
    }
}
