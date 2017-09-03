package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.NotificationAdapter;

/**
 * Notification presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.NotificationsView}.
 *
 * */

public interface NotificationsPresenter {

    // HTTP request.

    void requestNotifications(Context c, boolean refresh);
    void cancelRequest();

    // load data interface.

    /**
     * The param notify is used to control the SwipeRefreshLayout. If set true, the
     * SwipeRefreshLayout will show the refresh animation.
     * */
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    // manage HTTP request parameters.

    void setActivityForAdapter(MysplashActivity a);
    NotificationAdapter getAdapter();
}
