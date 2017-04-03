package com.wangdaye.mysplash.common.i.model;

/**
 * Multi-filter bar model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.MultiFilterBarView}.
 *
 * */

public interface MultiFilterBarModel {
    /** {@link com.wangdaye.mysplash.common.data.api.PhotoApi#getRandomPhotos(Integer, Boolean, String, String, String, int)} */

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
}
