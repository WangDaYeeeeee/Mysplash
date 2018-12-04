package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.common.data.service.network.FeedService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Trending model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.TrendingView}.
 *
 * */

public interface TrendingModel {

    PhotoAdapter getAdapter();
    FeedService getService();

    // manage HTTP request parameters.

    /** {@link com.wangdaye.mysplash.common.data.api.FeedApi#getTrendingFeed(String)} */
    String getFirstPage();
    String getNextPage();
    void setNextPage(String nextPage);

    // control load state.

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
