package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.common.ui.adapter.NotificationAdapter;

/**
 * Notifications model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.NotificationsView}.
 *
 * */

public interface NotificationsModel {

    NotificationAdapter getAdapter();

    // control load state.

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
}
