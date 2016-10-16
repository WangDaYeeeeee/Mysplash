package com.wangdaye.mysplash.main.model.widget;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.MultiFilterModel;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

import java.util.ArrayList;

/**
 * Multi-filter object.
 * */

public class MultiFilterObject
        implements MultiFilterModel {
    // data
    private PhotoAdapter adapter;
    private PhotoService service;

    private String searchQuery;
    private String searchUser;
    private int searchCategory;
    private String searchOrientation;
    private boolean searchFeatured;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    /** <br> life cycle. */

    public MultiFilterObject(Context c) {
        this.adapter = new PhotoAdapter(c, new ArrayList<Photo>());
        this.service = PhotoService.getService();

        this.searchQuery = "";
        this.searchUser = "";
        this.searchCategory = 0;
        this.searchOrientation = "";
        this.searchFeatured = false;

        this.refreshing = false;
        this.loading = false;
        this.over = false;
    }

    /** <br> model. */

    @Override
    public PhotoAdapter getAdapter() {
        return adapter;
    }

    @Override
    public PhotoService getService() {
        return service;
    }

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

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isOver() {
        return over;
    }

    @Override
    public void setOver(boolean over) {
        this.over = over;
    }
}
