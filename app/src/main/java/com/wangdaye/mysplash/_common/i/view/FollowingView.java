package com.wangdaye.mysplash._common.i.view;

/**
 * Following view.
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
