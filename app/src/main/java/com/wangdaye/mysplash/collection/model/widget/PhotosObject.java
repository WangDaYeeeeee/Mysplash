package com.wangdaye.mysplash.collection.model.widget;

import android.support.annotation.IntDef;

import com.wangdaye.mysplash.common.data.service.network.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotosModel;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

import java.util.List;

/**
 * Photos object.
 * */

public class PhotosObject
        implements PhotosModel {

    private PhotoAdapter adapter;
    private PhotoService service;

    private Object key;
    @TypeRule
    private int photosType;

    private int photosPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public static final int PHOTOS_TYPE_NORMAL = 0;
    public static final int PHOTOS_TYPE_CURATED = 1;
    @IntDef({PHOTOS_TYPE_NORMAL, PHOTOS_TYPE_CURATED})
    private @interface TypeRule {}

    public PhotosObject(PhotoAdapter adapter, Object key, @TypeRule int photosType) {
        this.adapter = adapter;
        this.service = PhotoService.getService();

        this.key = key;
        this.photosType = photosType;

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
    public PhotoService getService() {
        return service;
    }

    // control HTTP request parameters.

    @Override
    public Object getRequestKey() {
        return key;
    }

    @Override
    public void setRequestKey(Object key) {
        this.key = key;
    }

    @Override
    @TypeRule
    public int getPhotosType() {
        return photosType;
    }

    @Override
    public String getPhotosOrder() {
        return null;
    }

    @Override
    public void setPhotosOrder(String order) {
        // do nothing.
    }

    @Override
    public boolean isRandomType() {
        return false;
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
    public List<Integer> getPageList() {
        return null;
    }

    @Override
    public void setPageList(List<Integer> list) {
        // do nothing.
    }

    // control load state.

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
