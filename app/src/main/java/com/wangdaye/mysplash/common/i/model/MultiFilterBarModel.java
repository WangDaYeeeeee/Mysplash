package com.wangdaye.mysplash.common.i.model;

/**
 * Multi-filter bar model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.MultiFilterBarView}.
 *
 * */

public interface MultiFilterBarModel {
    /** {@link com.wangdaye.mysplash.common.data.api.PhotoApi#getRandomPhotos(Integer, Boolean, String, String, String, int)} */

    String getQuery();
    void setQuery(String query);

    String getUsername();
    void setUsername(String username);

    int getCategory();
    void setCategory(int c);

    String getOrientation();
    void setOrientation(String o);

    boolean isFeatured();
    void setFeatured(boolean f);
}
