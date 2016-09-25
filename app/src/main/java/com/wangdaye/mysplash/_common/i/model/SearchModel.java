package com.wangdaye.mysplash._common.i.model;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.data.service.SearchService;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

/**
 * Search model.
 * */

public interface SearchModel {

    RecyclerView.Adapter getAdapter();
    SearchService getService();
    void setActivity(Activity a);

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
