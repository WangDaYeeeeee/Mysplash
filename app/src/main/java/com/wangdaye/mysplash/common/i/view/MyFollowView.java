package com.wangdaye.mysplash.common.i.view;

/**
 * My follow view.
 *
 * A view which can request {@link com.wangdaye.mysplash.common.data.entity.unsplash.User} who
 * following user or followed by user and show them.
 *
 * */

public interface MyFollowView {

    void setRefreshingFollow(boolean refreshing);
    void setLoadingFollow(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestMyFollowSuccess();
    void requestMyFollowFailed(String feedback);
}
