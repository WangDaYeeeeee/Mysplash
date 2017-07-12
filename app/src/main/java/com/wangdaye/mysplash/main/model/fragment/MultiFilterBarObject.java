package com.wangdaye.mysplash.main.model.fragment;

import com.wangdaye.mysplash.common.i.model.MultiFilterBarModel;

/**
 * Multi-filter object.
 *
 * */

public class MultiFilterBarObject
        implements MultiFilterBarModel {

    private String searchQuery;
    private String searchUser;
    private int searchCategory;
    private String searchOrientation;
    private boolean searchFeatured;

    public MultiFilterBarObject() {
        this.searchQuery = "";
        this.searchUser = "";
        this.searchCategory = 0;
        this.searchOrientation = "";
        this.searchFeatured = false;
    }

    @Override
    public String getQuery() {
        return searchQuery;
    }

    @Override
    public void setQuery(String query) {
        searchQuery = query;
    }

    @Override
    public String getUsername() {
        return searchUser;
    }

    @Override
    public void setUsername(String username) {
        searchUser = username;
    }

    @Override
    public int getCategory() {
        return searchCategory;
    }

    @Override
    public void setCategory(int c) {
        searchCategory = c;
    }

    @Override
    public String getOrientation() {
        return searchOrientation;
    }

    @Override
    public void setOrientation(String o) {
        searchOrientation = o;
    }

    @Override
    public boolean isFeatured() {
        return searchFeatured;
    }

    @Override
    public void setFeatured(boolean f) {
        searchFeatured = f;
    }
}
