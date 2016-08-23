package com.wangdaye.mysplash._common.i.view;

/**
 * Collections view.
 * */

public interface CollectionsView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestCollectionsSuccess();
    void requestCollectionsFailed(String feedback);
}
