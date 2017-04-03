package com.wangdaye.mysplash.common.i.model;

import android.support.v7.widget.RecyclerView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.service.SearchService;
import com.wangdaye.mysplash.common._basic.MysplashActivity;

/**
 * Search model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.SearchView}.
 *
 * */

public interface SearchModel {

    RecyclerView.Adapter getAdapter();
    SearchService getService();
    void setActivity(MysplashActivity a);

    String getSearchQuery();
    void setSearchQuery(String query);

    int getPhotosPage();
    void setPhotosPage(@Mysplash.PageRule int page);

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
