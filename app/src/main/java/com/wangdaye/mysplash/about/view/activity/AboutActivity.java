package com.wangdaye.mysplash.about.view.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.AboutAdapter;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * About activity.
 * */

public class AboutActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {
    // widget
    private CoordinatorLayout container;
    private StatusBarView statusBar;
    private RecyclerView recyclerView;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initWidget();
        }
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
    public void onBackPressed() {
        finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
    }

    /** <br> UI. */

    private void initWidget() {
        this.container = (CoordinatorLayout) findViewById(R.id.activity_about_container);

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_about_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_about_statusBar);
        if (DisplayUtils.isNeedSetStatusBarMask()) {
            statusBar.setBackgroundResource(R.color.colorPrimary_light);
            statusBar.setMask(true);
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.activity_about_recyclerView);
        recyclerView.setAdapter(new AboutAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    /** <br> interface. */

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return SwipeBackCoordinatorLayout.canSwipeBackForThisView(recyclerView, dir);
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

    // snackbar container.

    @Override
    public View getSnackbarContainer() {
        return container;
    }
}
