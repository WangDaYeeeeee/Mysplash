package com.wangdaye.mysplash._common.i.view;

/**
 * Category view.
 * */

public interface CategoryView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestPhotosSuccess();
    void requestPhotosFailed(String feedback);
}
