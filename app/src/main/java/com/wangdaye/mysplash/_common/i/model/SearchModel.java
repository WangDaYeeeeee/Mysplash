package com.wangdaye.mysplash._common.i.model;

import android.app.Activity;

import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

/**
 * Search model.
 * */

public interface SearchModel {

    PhotoAdapter getAdapter();
    PhotoService getService();
    void setActivity(Activity a);

    String getSearchQuery();
    void setSearchQuery(String query);

    String getSearchOrientation();
    void setSearchOrientation(String orientation);

    int getPhotosPage();
    void setPhotosPage(int page);

    boolean isLoading();
    void setLoading(boolean loading);

    boolean isOver();
    void setOver(boolean over);
}
