package com.wangdaye.mysplash.common.i.view;

/**
 * Following view.
 *
 * A view which can request
 * {@link com.wangdaye.mysplash.common.data.entity.unsplash.FollowingFeedResult} and show them.
 *
 * */

public interface FollowingView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestFollowingFeedSuccess();
    void requestFollowingFeedFailed(String feedback);
}
