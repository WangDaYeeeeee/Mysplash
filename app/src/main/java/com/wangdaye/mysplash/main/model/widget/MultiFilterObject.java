package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.MultiFilterModel;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Multi-filter object.
 *
 * A {@link MultiFilterModel} for
 * {@link com.wangdaye.mysplash.main.view.widget.MultiFilterPhotosView}.
 *
 * */

public class MultiFilterObject
        implements MultiFilterModel {

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

    public MultiFilterObject(PhotoAdapter a) {
        this.adapter = a;
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
