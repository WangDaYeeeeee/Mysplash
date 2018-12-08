package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.service.network.FeedService;
import com.wangdaye.mysplash.common.ui.adapter.FollowingAdapter;

/**
 * Following model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.FollowingView}.
 *
 * */

public interface FollowingModel {

    FollowingAdapter getAdapter();
    FeedService getService();

    // manage HTTP request parameters.

    int getPhotosPage();
    void setPhotosPage(@Mysplash.PageRule int page);

    // control load state.

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
