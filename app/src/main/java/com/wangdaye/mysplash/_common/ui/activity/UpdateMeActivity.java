package com.wangdaye.mysplash._common.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Me;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash._common.ui.dialog.RateLimitDialog;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.widget.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Update me activity.
 * */

public class UpdateMeActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        SafeHandler.HandlerContainer, UserService.OnRequestMeProfileListener {
    // widget
    private SafeHandler<UpdateMeActivity> handler;

    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private NestedScrollView scrollView;
    private CircularProgressView progressView;
    private LinearLayout contentView;

    private EditText usernameTxt;
    private EditText firstNameTxt;
    private EditText lastNameTxt;
    private EditText emailTxt;
    private EditText portfolioTxt;
    private EditText locationTxt;
    private EditText bioTxt;

    // data
    private UserService service;
    private boolean backPressed = false;

    private int state;
    private final int INPUT_STATE = 0;
    private final int UPDATE_STATE = 1;

    private final String KEY_UPDATE_ME_ACTIVITY_USERNAME = "update_me_activity_username";
    private final String KEY_UPDATE_ME_ACTIVITY_FIRSTNAME = "update_me_activity_firstname";
    private final String KEY_UPDATE_ME_ACTIVITY_LASTNAME = "update_me_activity_lastname";
    private final String KEY_UPDATE_ME_ACTIVITY_EMAIL = "update_me_activity_email";
    private final String KEY_UPDATE_ME_ACTIVITY_PORTFOLIO = "update_me_activity_portfolio";
    private final String KEY_UPDATE_ME_ACTIVITY_LOCATION = "update_me_activity_location";
    private final String KEY_UPDATE_ME_ACTIVITY_BIO = "update_me_activity_bio";

    /** <br> life cycle. */

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
        if (Mysplash.getInstance().isLightTheme()) {
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
    protected boolean needSetStatusBarTextDark() {
        return true;
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
    public void handleBackPressed() {
        if (state == INPUT_STATE && backPressed) {
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        } else if (state == INPUT_STATE) {
            backPressed = true;
            NotificationUtils.showSnackbar(
                    getString(R.string.feedback_click_again_to_exit),
                    Snackbar.LENGTH_SHORT);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.obtainMessage(1).sendToTarget();
                }
            }, 2000);
        }
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> UI. */

    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        this.container = (CoordinatorLayout) findViewById(R.id.activity_update_me_container);

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_update_me_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_update_me_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.scrollView = (NestedScrollView) findViewById(R.id.activity_update_me_scrollView);

        ImageButton closeBtn = (ImageButton) findViewById(R.id.container_update_me_closeBtn);
        if (Mysplash.getInstance().isLightTheme()) {
            closeBtn.setImageResource(R.drawable.ic_close_light);
        } else {
            closeBtn.setImageResource(R.drawable.ic_close_dark);
        }
        closeBtn.setOnClickListener(this);

        this.progressView = (CircularProgressView) findViewById(R.id.container_update_me_progressView);
        progressView.setVisibility(View.GONE);

        this.contentView = (LinearLayout) findViewById(R.id.container_update_me_textContainer);
        contentView.setVisibility(View.VISIBLE);

        this.usernameTxt = (EditText) findViewById(R.id.container_update_me_usernameTxt);
        DisplayUtils.setTypeface(this, usernameTxt);

        this.firstNameTxt = (EditText) findViewById(R.id.container_update_me_firstNameTxt);
        DisplayUtils.setTypeface(this, firstNameTxt);

        this.lastNameTxt = (EditText) findViewById(R.id.container_update_me_lastNameTxt);
        DisplayUtils.setTypeface(this, lastNameTxt);

        this.emailTxt = (EditText) findViewById(R.id.container_update_me_emailTxt);
        DisplayUtils.setTypeface(this, emailTxt);

        this.portfolioTxt = (EditText) findViewById(R.id.container_update_me_portfolioTxt);
        DisplayUtils.setTypeface(this, portfolioTxt);

        this.locationTxt = (EditText) findViewById(R.id.container_update_me_locationTxt);
        DisplayUtils.setTypeface(this, locationTxt);

        this.bioTxt = (EditText) findViewById(R.id.container_update_me_bioTxt);
        DisplayUtils.setTypeface(this, bioTxt);

        Button saveBtn = (Button) findViewById(R.id.container_update_me_saveBtn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveBtn.setBackgroundResource(R.drawable.button_login);
        }
        saveBtn.setOnClickListener(this);

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

    /** <br> data. */

    private void initData() {
        this.service = UserService.getService();
        this.state = INPUT_STATE;
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
            NotificationUtils.showSnackbar(
                    getString(R.string.feedback_name_is_required),
                    Snackbar.LENGTH_SHORT);
        }
    }

    /** <br> animator. */

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

    /** <br> interface. */

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_update_me_closeBtn:
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;

            case R.id.container_update_me_saveBtn:
                updateProfile();
                break;
        }
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBackForThisView(scrollView, dir);
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

    // on request me profile listener.

    @Override
    public void onRequestMeProfileSuccess(Call<Me> call, Response<Me> response) {
        if (response.isSuccessful() && response.body() != null) {
            AuthManager.getInstance().writeUserInfo(response.body());
            finish();
            overridePendingTransition(0, R.anim.activity_slide_out_bottom);
        } else {
            setState(INPUT_STATE);
            NotificationUtils.showSnackbar(
                    getString(R.string.feedback_update_profile_failed),
                    Snackbar.LENGTH_SHORT);
            RateLimitDialog.checkAndNotify(
                    this,
                    response.headers().get("X-Ratelimit-Remaining"));
        }
    }

    @Override
    public void onRequestMeProfileFailed(Call<Me> call, Throwable t) {
        setState(INPUT_STATE);
        NotificationUtils.showSnackbar(
                getString(R.string.feedback_update_profile_failed),
                Snackbar.LENGTH_SHORT);
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
