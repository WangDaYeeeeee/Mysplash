package com.wangdaye.mysplash.main.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash.common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash.common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.main.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.main.view.widget.NotificationsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Notification activity.
 *
 * This activity is used to show user's notifications.
 *
 * */

public class NotificationActivity extends MysplashActivity
        implements SwipeBackManageView,
        View.OnClickListener, Toolbar.OnMenuItemClickListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R.id.activity_notification_container)
    CoordinatorLayout container;

    @BindView(R.id.activity_notification_shadow)
    View shadow;

    @BindView(R.id.activity_notification_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.activity_notification_notificationsView)
    NotificationsView notificationsView;

    private SwipeBackManagePresenter swipeBackManagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initPresenter();
        ButterKnife.bind(this);
        AuthManager.getInstance()
                .getNotificationManager()
                .addOnUpdateNotificationListener(
                        notificationsView.getOnUpdateNotificationListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthManager.getInstance()
                .getNotificationManager()
                .removeOnUpdateNotificationListener(
                        notificationsView.getOnUpdateNotificationListener());
        notificationsView.cancelRequest();
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
        if (notificationsView.needPagerBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishSelf(true);
        }
    }

    @Override
    public void backToTop() {
        notificationsView.pagerScrollToTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        AuthManager.getInstance()
                .getNotificationManager()
                .setLatestSeenTime();

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

    private void initPresenter() {
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        SwipeBackCoordinatorLayout swipeBackView = ButterKnife.findById(
                this, R.id.activity_notification_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.activity_notification_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        ThemeManager.inflateMenu(
                toolbar, R.menu.activity_notification_light, R.menu.activity_notification_dark);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);

        notificationsView.setActivity(this);
        notificationsView.initRefresh();
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                finishSelf(true);
                break;
        }
    }

    @OnClick(R.id.activity_notification_toolbar) void clickToolbar() {
        backToTop();
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                AuthManager.getInstance()
                        .getNotificationManager()
                        .clearNotifications(false);
                notificationsView.initRefresh();
                break;
        }
        return true;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        statusBar.setAlpha(1 - percent);
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // view.

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return notificationsView.canSwipeBack(dir);
        } else {
            return notificationsView.canSwipeBack(dir);
        }
    }
}
