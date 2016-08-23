package com.wangdaye.mysplash._common.i.view;

/**
 * Search view.
 * */

public interface SearchView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void showButton();
    void setBackgroundOpacity();

    void initRefreshStart();
    void requestPhotosSuccess();
    void requestPhotosFailed(String feedback);
}
