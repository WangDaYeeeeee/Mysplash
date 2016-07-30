package com.wangdaye.mysplash.main.model.widget.i;

import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.main.adapter.PhotosAdapter;

import java.util.List;

/**
 * Photo state model.
 * */

public interface PhotoStateModel {

    PhotosAdapter getAdapter();
    void setAdapter(PhotosAdapter a);

    PhotoService getPhotoService();
    void cancelRequest();

    String getOrder();
    void setOrder(String o);

    boolean isNormalMode();
    void setNormalMode(boolean normal);

    boolean isLoadingData();
    void setLoadingData(boolean loading);

    boolean isLoadFinish();
    void setLoadFinish(boolean finish);

    int getPage();
    void setPage(int p);

    List<Integer> getPageList();
    void setPageList(List<Integer> l);

    int getCategoryId();
    void setCategoryId(int id);

    String getSearchQuery();
    void setSearchQuery(String q);

    String getOrientation();
    void setOrientation(String o);
}
