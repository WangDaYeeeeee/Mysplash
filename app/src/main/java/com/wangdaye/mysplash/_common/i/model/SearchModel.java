package com.wangdaye.mysplash._common.i.model;

import android.support.v7.widget.RecyclerView;

import com.wangdaye.mysplash._common.data.service.SearchService;
import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;

/**
 * Search model.
 * */

public interface SearchModel {

    RecyclerView.Adapter getAdapter();
    SearchService getService();
    void setActivity(MysplashActivity a);

    String getSearchQuery();
    void setSearchQuery(String query);

    int getPhotosPage();
    void setPhotosPage(int page);

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    boolean isOver();
    void setOver(boolean over);
}
