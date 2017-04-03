package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

import java.util.List;

/**
 * Photos model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.PhotosView}.
 *
 * */

public interface PhotosModel {

    PhotoAdapter getAdapter();
    PhotoService getService();

    Object getRequestKey();
    void setRequestKey(Object key);

    int getPhotosType();
    String getPhotosOrder();

    void setPhotosOrder(String order);
    boolean isRandomType();

    int getPhotosPage();
    void setPhotosPage(@Mysplash.PageRule int page);

    /** {@link com.wangdaye.mysplash.common.utils.ValueUtils#getPageListByCategory(int)} */
    List<Integer> getPageList();
    void setPageList(List<Integer> list);

    boolean isRefreshing();
    void setRefreshing(boolean refreshing);

    boolean isLoading();
    void setLoading(boolean loading);

    /** The flag to mark the photos already load over. */
    boolean isOver();
    void setOver(boolean over);
}
