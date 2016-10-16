package com.wangdaye.mysplash.main.model.widget;

import android.app.Activity;
import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.service.SearchService;
import com.wangdaye.mysplash._common.i.model.SearchModel;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

import java.util.ArrayList;

/**
 * Search object.
 * */

public class SearchPhotosObject
        implements SearchModel {
    // data
    private PhotoAdapter adapter;
    private SearchService service;

    private String searchQuery;

    private int photosPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    /** <br> life cycle. */

    public SearchPhotosObject(Context c) {
        this.adapter = new PhotoAdapter(c, new ArrayList<Photo>());
        this.service = SearchService.getService();

        this.searchQuery = "";

        this.photosPage = 0;

        this.refreshing = false;
        this.loading = false;
        this.over = false;
    }

    @Override
    public PhotoAdapter getAdapter() {
        return adapter;
    }

    @Override
    public SearchService getService() {
        return service;
    }

    @Override
    public void setActivity(Activity a) {
        adapter.setActivity(a);
    }

    @Override
    public String getSearchQuery() {
        return searchQuery;
    }

    @Override
    public void setSearchQuery(String query) {
        searchQuery = query;
    }

    @Override
    public int getPhotosPage() {
        return photosPage;
    }

    @Override
    public void setPhotosPage(int page) {
        photosPage = page;
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
