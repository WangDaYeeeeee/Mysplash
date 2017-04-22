package com.wangdaye.mysplash.main.model.widget;

import android.content.Context;

import com.wangdaye.mysplash.common.i.model.NotificationsModel;
import com.wangdaye.mysplash.common.ui.adapter.NotificationAdapter;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;

/**
 * Notification object.
 * */

public class NotificationsObject implements NotificationsModel {

    private NotificationAdapter adapter;

    private boolean refreshing;
    private boolean loading;

    public NotificationsObject(Context a) {
        this.adapter = new NotificationAdapter(a);

        this.refreshing = false;
        this.loading = false;
    }

    @Override
    public NotificationAdapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isOver() {
        return AuthManager.getInstance().getNotificationManager().isLoadFinish();
    }
}
