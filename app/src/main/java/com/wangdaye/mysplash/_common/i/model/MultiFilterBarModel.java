package com.wangdaye.mysplash._common.i.model;

/**
 * Multi-filter bar model.
 * */

public interface MultiFilterBarModel {

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
