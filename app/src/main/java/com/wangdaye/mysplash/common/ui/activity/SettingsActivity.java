package com.wangdaye.mysplash.common.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.fragment.SettingsFragment;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Settings activity.
 *
 * This activity is used to show and save setting options.
 *
 * */

public class SettingsActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_settings_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_settings_statusBar)
    StatusBarView statusBar;

    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            ButterKnife.bind(this);
            initWidget();
            fragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.activity_settings_preferenceContainer, fragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Mysplash.CUSTOM_API_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    showSnackbar(
                            getString(R.string.feedback_please_login),
                            Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    protected void setTheme() {
        if (ThemeManager.getInstance(this).isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Settings);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Settings);
        }
    }

    @Override
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    public void finishActivity(int dir) {
        SwipeBackCoordinatorLayout.hideBackgroundShadow(container);
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
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initWidget() {
        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_settings_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        toolbar.setNavigationOnClickListener(this);
    }

    // control.

    private void showSnackbar(String content, int duration) {
        View container = provideSnackbarContainer();

        Snackbar snackbar = Snackbar
                .make(container, content, duration);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(ThemeManager.getRootColor(this));

        TextView contentTxt = ButterKnife.findById(snackbarLayout, R.id.snackbar_text);
        DisplayUtils.setTypeface(this, contentTxt);
        contentTxt.setTextColor(ThemeManager.getContentColor(this));

        snackbar.show();
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;
        }
    }

    // on swipe back listener.

    @Override
    public boolean canSwipeBack(int dir) {
        ListView listView = fragment.getScrolledView();
        return listView != null
                && SwipeBackCoordinatorLayout.canSwipeBack(listView, dir);
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
}
