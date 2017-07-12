package com.wangdaye.mysplash.search.model.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.data.service.SearchService;
import com.wangdaye.mysplash.common.i.model.SearchModel;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Search photos object.
 *
 * */

public class SearchPhotosObject
        implements SearchModel {

    private PhotoAdapter adapter;
    private SearchService service;

    private String searchQuery;

    private int photosPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public SearchPhotosObject(PhotoAdapter adapter) {
        this.adapter = adapter;
        this.service = SearchService.getService();

        this.searchQuery = "";

        this.photosPage = adapter.getRealItemCount() / Mysplash.DEFAULT_PER_PAGE;

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
    public void setActivity(MysplashActivity a) {
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
