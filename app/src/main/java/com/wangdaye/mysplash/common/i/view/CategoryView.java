package com.wangdaye.mysplash.common.i.view;

/**
 * Category view.
 *
 * A view which can request {@link com.wangdaye.mysplash.common.data.entity.unsplash.Photo} in
 * a category and show them.
 *
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
