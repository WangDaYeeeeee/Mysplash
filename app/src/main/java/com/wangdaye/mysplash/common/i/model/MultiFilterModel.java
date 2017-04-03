package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Multi-filter model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.MultiFilterView}.
 *
 * */

public interface MultiFilterModel {
    /** {@link com.wangdaye.mysplash.common.data.api.PhotoApi#getRandomPhotos(Integer, Boolean, String, String, String, int)} */

    PhotoAdapter getAdapter();
    PhotoService getService();

    void setQuery(String query);
    String getQuery();

    void setUsername(String username);
    String getUsername();

    void setCategory(int c);
    int getCategory();

    void setOrientation(String o);
    String getOrientation();

    void setFeatured(boolean f);
    boolean isFeatured();

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
