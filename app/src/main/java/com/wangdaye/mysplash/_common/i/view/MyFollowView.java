package com.wangdaye.mysplash._common.i.view;

/**
 * My follow view.
 * */

public interface MyFollowView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestMyFollowSuccess();
    void requestMyFollowFailed(String feedback);
}
