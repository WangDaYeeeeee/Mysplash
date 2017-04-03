package com.wangdaye.mysplash.common.i.view;

/**
 * Collections view.
 *
 * A view which can request {@link com.wangdaye.mysplash.common.data.entity.unsplash.Collection}
 * data and show them.
 *
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
