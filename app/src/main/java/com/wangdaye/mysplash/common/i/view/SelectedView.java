package com.wangdaye.mysplash.common.i.view;

/**
 * Selected view.
 *
 * A view which can request {@link com.wangdaye.mysplash.common.data.entity.unsplash.Collection}
 * data and show them.
 *
 * */

public interface SelectedView {

    void setRefreshingCollection(boolean refreshing);
    void setLoadingCollection(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestCollectionsSuccess();
    void requestCollectionsFailed(String feedback);
}
