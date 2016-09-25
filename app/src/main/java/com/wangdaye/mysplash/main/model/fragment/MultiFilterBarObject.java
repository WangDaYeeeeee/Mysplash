package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash._common.i.model.MultiFilterBarModel;

/**
 * Multi-filter object.
 * */

public class MultiFilterBarObject
        implements MultiFilterBarModel {
    // data
    private String searchQuery;
    private String searchUser;
    private int searchCategory;
    private String searchOrientation;
    private boolean searchFeatured;

    /** <br> life cycle. */

    public MultiFilterBarObject() {

        this.searchQuery = "";
        this.searchUser = "";
        this.searchCategory = 0;
        this.searchOrientation = "";
        this.searchFeatured = false;
    }

    /** <br> model. */

    @Override
    public void setQuery(String query) {
        searchQuery = query;
    }

    @Override
    public String getQuery() {
        return searchQuery;
    }

    @Override
    public void setUsername(String username) {
        searchUser = username;
    }

    @Override
    public String getUsername() {
        return searchUser;
    }

    @Override
    public void setCategory(int c) {
        searchCategory = c;
    }

    @Override
    public int getCategory() {
        return searchCategory;
    }

    @Override
    public void setOrientation(String o) {
        searchOrientation = o;
    }

    @Override
    public String getOrientation() {
        return searchOrientation;
    }

    @Override
    public void setFeatured(boolean f) {
        searchFeatured = f;
    }

    @Override
    public boolean isFeatured() {
        return searchFeatured;
    }
}
