package com.wangdaye.mysplash._common.i.view;

/**
 * Photos view.
 * */

public interface PhotosView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestPhotosSuccess();
    void requestPhotosFailed(String feedback);
}
