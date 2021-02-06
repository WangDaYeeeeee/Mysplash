package com.wangdaye.me.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wangdaye.base.unsplash.Me;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.common.network.observer.BaseObserver;
import com.wangdaye.common.network.service.UserService;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.utils.FullscreenInputWorkaround;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.AuthManager;
import com.wangdaye.me.R;
import com.wangdaye.me.R2;
import com.wangdaye.me.di.component.DaggerApplicationComponent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Emitter;
import io.reactivex.Observable;

/**
 * Update me activity.
 *
 * This activity is used to update {@link Me}.
 *
 * */

@Route(path = UpdateMeActivity.UPDATE_ME_ACTIVITY)
public class UpdateMeActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_update_me_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_update_me_container) CoordinatorLayout container;

    @BindView(R2.id.activity_update_me_scrollView) NestedScrollView scrollView;
    @BindView(R2.id.container_update_me_progressView) CircularProgressView progressView;
    @BindView(R2.id.container_update_me_textContainer) LinearLayout contentView;

    @OnClick(R2.id.container_update_me_closeBtn) void close() {
        finishSelf(true);
    }
    @OnClick(R2.id.container_update_me_saveBtn) void save() {
        updateProfile();
    }

    @BindView(R2.id.container_update_me_usernameTxtContainer) TextInputLayout usernameTextContainer;
    private TextInputEditText usernameTxt;
    private TextInputEditText firstNameTxt;
    private TextInputEditText lastNameTxt;
    private TextInputEditText emailTxt;
    private TextInputEditText portfolioTxt;
    private TextInputEditText locationTxt;
    private TextInputEditText bioTxt;

    private FullscreenInputWorkaround workaround;

    @Inject UserService service;

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

    public static final String UPDATE_ME_ACTIVITY = "/me/UpdateMeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_me);
        ButterKnife.bind(this);
        initData();
        initWidget(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.cancel();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (usernameTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_USERNAME, usernameTxt.getText().toString());
        }
        if (firstNameTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_FIRSTNAME, firstNameTxt.getText().toString());
        }
        if (lastNameTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_LASTNAME, lastNameTxt.getText().toString());
        }
        if (emailTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_EMAIL, emailTxt.getText().toString());
        }
        if (portfolioTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_PORTFOLIO, portfolioTxt.getText().toString());
        }
        if (locationTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_LOCATION, locationTxt.getText().toString());
        }
        if (bioTxt.getText() != null) {
            outState.putString(KEY_UPDATE_ME_ACTIVITY_BIO, bioTxt.getText().toString());
        }
    }

    @Override
    public void handleBackPressed() {
        if (state == INPUT_STATE && backPressed) {
            finishSelf(true);
        } else if (state == INPUT_STATE) {
            backPressed = true;
            NotificationHelper.showSnackbar(this, getString(R.string.feedback_click_again_to_exit));

            Observable.create(Emitter::onComplete)
                    .compose(RxLifecycle.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .delay(2, TimeUnit.SECONDS)
                    .doOnComplete(() -> backPressed = false)
                    .subscribe();
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
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initData() {
        this.state = INPUT_STATE;
    }

    private void initWidget(Bundle savedInstanceState) {
        this.workaround = FullscreenInputWorkaround.assistActivity(
                this, container, null);

        swipeBackView.setOnSwipeListener(this);

        progressView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);

        this.usernameTxt = findViewById(R.id.container_update_me_usernameTxt);

        this.firstNameTxt = findViewById(R.id.container_update_me_firstNameTxt);

        this.lastNameTxt = findViewById(R.id.container_update_me_lastNameTxt);

        this.emailTxt = findViewById(R.id.container_update_me_emailTxt);

        this.portfolioTxt = findViewById(R.id.container_update_me_portfolioTxt);

        this.locationTxt = findViewById(R.id.container_update_me_locationTxt);

        this.bioTxt = findViewById(R.id.container_update_me_bioTxt);

        Button saveBtn = findViewById(R.id.container_update_me_saveBtn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveBtn.setBackgroundResource(R.drawable.button_login);
        }

        usernameTxt.setOnFocusChangeListener((v, hasFocus) -> usernameTextContainer.setError(null));
        if (savedInstanceState == null) {
            usernameTxt.setText(AuthManager.getInstance().getUsername());
            firstNameTxt.setText(AuthManager.getInstance().getFirstName());
            lastNameTxt.setText(AuthManager.getInstance().getLastName());
            emailTxt.setText(AuthManager.getInstance().getEmail());
            portfolioTxt.setText(AuthManager.getInstance().getUser().portfolio_url);
            locationTxt.setText(AuthManager.getInstance().getUser().location);
            bioTxt.setText(AuthManager.getInstance().getUser().bio);
        } else {
            usernameTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_USERNAME,
                            AuthManager.getInstance().getUsername()));
            firstNameTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_FIRSTNAME,
                            AuthManager.getInstance().getFirstName()));
            lastNameTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_LASTNAME,
                            AuthManager.getInstance().getLastName()));
            emailTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_EMAIL,
                            AuthManager.getInstance().getEmail()));
            portfolioTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_PORTFOLIO,
                            AuthManager.getInstance().getUser().portfolio_url));
            locationTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_LOCATION,
                            AuthManager.getInstance().getUser().location));
            bioTxt.setText(
                    savedInstanceState.getString(
                            KEY_UPDATE_ME_ACTIVITY_BIO,
                            AuthManager.getInstance().getUser().bio));
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
        if (usernameTxt.getText() != null && !TextUtils.isEmpty(usernameTxt.getText().toString())
                && firstNameTxt.getText() != null
                && lastNameTxt.getText() != null
                && emailTxt.getText() != null
                && portfolioTxt.getText() != null
                && locationTxt.getText() != null
                && bioTxt.getText() != null) {
            service.updateMeProfile(
                    usernameTxt.getText().toString(),
                    firstNameTxt.getText().toString(),
                    lastNameTxt.getText().toString(),
                    emailTxt.getText().toString(),
                    portfolioTxt.getText().toString(),
                    locationTxt.getText().toString(),
                    bioTxt.getText().toString(),
                    new BaseObserver<Me>() {
                        @Override
                        public void onSucceed(Me me) {
                            AuthManager.getInstance().updateMe(me);
                            AuthManager.getInstance().requestPersonalProfile();
                            finishSelf(true);
                        }

                        @Override
                        public void onFailed() {
                            setState(INPUT_STATE);
                            NotificationHelper.showSnackbar(
                                    UpdateMeActivity.this,
                                    getString(R.string.feedback_update_profile_failed)
                            );
                        }
                    }
            );
            setState(UPDATE_STATE);
        } else {
            usernameTextContainer.setError(getString(R.string.feedback_name_is_required));
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
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1, 0)
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

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBack(scrollView, dir);
    }

    @Override
    public void onSwipeProcess(@SwipeBackCoordinatorLayout.DirectionRule int dir, float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
