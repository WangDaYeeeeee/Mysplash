package com.wangdaye.mysplash.user.model.widget;

import android.app.Activity;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.data.entity.Photo;
import com.wangdaye.mysplash._common.data.entity.User;
import com.wangdaye.mysplash._common.data.service.PhotoService;
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

    private User requestKey;

    private int photosType;
    private String photosOrder;

    private int photosPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public static final int PHOTOS_TYPE_PHOTOS = 0;
    public static final int PHOTOS_TYPE_LIKES = 1;

    /** <br> life cycle. */

    public PhotosObject(Activity a, int photosType) {
        this.adapter = new PhotoAdapter(a, new ArrayList<Photo>());
        this.service = PhotoService.getService();

        this.requestKey = Mysplash.getInstance().getUser();

        this.photosType = photosType;
        this.photosOrder = PhotoApi.ORDER_BY_LATEST;

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
        return requestKey;
    }

    @Override
    public void setRequestKey(Object key) {
        requestKey = (User) key;
    }

    @Override
    public int getPhotosType() {
        return photosType;
    }

    @Override
    public String getPhotosOrder() {
        return photosOrder;
    }

    @Override
    public void setPhotosOrder(String order) {
        photosOrder = order;
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
