package com.wangdaye.mysplash.common.i.view;

/**
 * Search view.
 *
 * A view which can request data by {@link com.wangdaye.mysplash.common.data.api.SearchApi} and
 * show them.
 *
 * */

public interface SearchView {

    void setRefreshingSearchItem(boolean refreshing);
    void setLoadingSearchItem(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void searchSuccess();
    void searchFailed(String feedback);
}
