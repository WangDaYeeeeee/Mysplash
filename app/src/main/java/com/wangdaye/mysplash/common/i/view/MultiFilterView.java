package com.wangdaye.mysplash.common.i.view;

/**
 * Multi-filter view.
 *
 * A view which can request {@link com.wangdaye.mysplash.common.data.entity.unsplash.Photo} random
 * by multiple params and show them.
 *
 * */

public interface MultiFilterView {

    void setRefreshing(boolean refreshing);
    void setLoading(boolean loading);

    void setPermitRefreshing(boolean permit);
    void setPermitLoading(boolean permit);

    void initRefreshStart();
    void requestPhotosSuccess();
    void requestPhotosFailed(String feedback);
}
