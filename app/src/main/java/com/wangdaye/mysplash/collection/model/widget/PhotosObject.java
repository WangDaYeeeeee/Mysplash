package com.wangdaye.mysplash.collection.model.widget;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.Collection;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.utils.AuthManager;
import com.wangdaye.mysplash._common.i.model.PhotosModel;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Photos object.
 * */

public class PhotosObject
        implements PhotosModel {
    // data
    private PhotoAdapter adapter;
    private PhotoService service;

    private Object key;
    private int photosType;

    private int photosPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public static final int PHOTOS_TYPE_NORMAL = 0;
    public static final int PHOTOS_TYPE_CURATED = 1;

    /** <br> life cycle. */

    public PhotosObject(Context c, Object key, int photosType) {
        this.adapter = new PhotoAdapter(c, new ArrayList<Photo>());
        adapter.setInMyCollection(
                AuthManager.getInstance().getUsername() != null
                        && AuthManager.getInstance().getUsername().equals(((Collection) key).user.username));

        this.service = PhotoService.getService();

        this.key = key;
        this.photosType = photosType;

        this.photosPage = 0;

        this.refreshing = false;
        this.loading = false;
        this.over = false;
    }

    /** <br> model. */

    @Override
    public PhotoAdapter getAdapter() {
        return adapter;
    }

    @Override
    public PhotoService getService() {
        return service;
    }

    @Override
    public Object getRequestKey() {
        return key;
    }

    @Override
    public void setRequestKey(Object key) {
        this.key = key;
    }

    @Override
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
