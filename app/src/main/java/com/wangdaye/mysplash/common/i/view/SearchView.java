package com.wangdaye.mysplash.common.i.view;

/**
 * Search view.
 *
 * A view which can request data by {@link com.wangdaye.mysplash.common.data.api.SearchApi} and
 * show them.
 *
 * */

public interface SearchView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestPhotosSuccess();
    void requestPhotosFailed(String feedback);
}
