package com.wangdaye.settings.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wangdaye.common.base.activity.MysplashActivity;
import com.wangdaye.settings.R;
import com.wangdaye.settings.R2;
import com.wangdaye.settings.fragment.SettingsFragment;
import com.wangdaye.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.wangdaye.common.ui.widget.windowInsets.StatusBarView;
import com.wangdaye.common.utils.helper.NotificationHelper;
import com.wangdaye.common.utils.manager.ThemeManager;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Settings activity.
 *
 * This activity is used to show and save setting options.
 *
 * */

@Route(path = SettingsActivity.SETTINGS_ACTIVITY)
public class SettingsActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_settings_container) CoordinatorLayout container;
    @BindView(R2.id.activity_settings_statusBar) StatusBarView statusBar;

    public static final String SETTINGS_ACTIVITY = "/settings/SettingsActivity";

    public static final int ACTIVITY_REQUEST_CODE_CUSTOM_API = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initWidget();
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_settings_preferenceContainer, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_CUSTOM_API:
                if (resultCode == RESULT_OK) {
                    NotificationHelper.showSnackbar(
                            this, getString(R.string.feedback_please_login));
                }
                break;
        }
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
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

    private void initWidget() {
        SwipeBackCoordinatorLayout swipeBackView = findViewById(R.id.activity_settings_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = findViewById(R.id.activity_settings_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        toolbar.setNavigationOnClickListener(v -> finishSelf(true));
    }

    // interface.

    // on swipe back listener.

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
