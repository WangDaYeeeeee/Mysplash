package com.wangdaye.mysplash._common.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.fragment.SettingsFragment;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Settings activity.
 * */

public class SettingsActivity extends MysplashActivity
        implements View.OnClickListener, SwipeBackCoordinatorLayout.OnSwipeListener {
    // widget
    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private SettingsFragment fragment;

    /** <br> life cycle. */

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
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Settings);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Settings);
        }
    }

    @Override
    protected void backToTop() {
        // do nothing.
    }

    @Override
    protected boolean isFullScreen() {
        return true;
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
    public void handleBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    @Override
    public View getSnackbarContainer() {
        return container;
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

    /** <br> UI. */

    private void initWidget() {
        this.container = (CoordinatorLayout) findViewById(R.id.activity_settings_container);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_settings_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        SwipeBackCoordinatorLayout swipeBackView = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_settings_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setNavigationOnClickListener(this);
    }

    private void showSnackbar(String content, int duration) {
        View container = provideSnackbarContainer();

        Snackbar snackbar = Snackbar
                .make(container, content, duration);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView contentTxt = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
        DisplayUtils.setTypeface(this, contentTxt);

        if (Mysplash.getInstance().isLightTheme()) {
            contentTxt.setTextColor(ContextCompat.getColor(this, R.color.colorTextContent_light));
            snackbarLayout.setBackgroundResource(R.color.colorRoot_light);
        } else {
            contentTxt.setTextColor(ContextCompat.getColor(this, R.color.colorTextContent_dark));
            snackbarLayout.setBackgroundResource(R.color.colorRoot_dark);
        }

        snackbar.show();
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
                break;
        }
    }

    // on swipe back swipeListener.

    @Override
    public boolean canSwipeBack(int dir) {
        ListView listView = fragment.getScrolledView();
        return listView != null
                && SwipeBackCoordinatorLayout.canSwipeBackForThisView(listView, dir);
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
