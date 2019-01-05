package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Multi-filter model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.MultiFilterView}.
 *
 * */

public interface MultiFilterModel {
    /** {@link com.wangdaye.mysplash.common.data.api.PhotoApi#getRandomPhotos(String, Boolean, String, String, String, int)} */

    PhotoAdapter getAdapter();
    PhotoService getService();

    // manage HTTP request parameters.

    String getQuery();
    void setQuery(String query);

    String getUsername();
    void setUsername(String username);

    String getOrientation();
    void setOrientation(String o);

    boolean isFeatured();
    void setFeatured(boolean f);

    // control load state.

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
