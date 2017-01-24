package com.wangdaye.mysplash._common.i.model;

import com.wangdaye.mysplash._common.data.service.FollowingService;
import com.wangdaye.mysplash._common.ui.adapter.FollowingAdapter;

/**
 * Following model.
 * */

public interface FollowingModel {

    FollowingAdapter getAdapter();
    FollowingService getService();

    String getNextPage();
    void setNextPage(String nextPage);

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    boolean isOver();
    void setOver(boolean over);
}
