package com.wangdaye.mysplash.main.model.widget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.Photo;
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

    private int photosType;
    private String photosOrder;

    private int photosPage;
    private List<Integer> pageList;

    private boolean loading;
    private boolean over;

    public static final int PHOTOS_TYPE_NEW = 0;
    public static final int PHOTOS_TYPE_FEATURED = 1;
    private final String RANDOM_TXT;

    /** <br> life cycle. */

    public PhotosObject(Activity a, int photosType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(a);

        this.adapter = new PhotoAdapter(a, new ArrayList<Photo>());
        this.service = PhotoService.getService();

        this.photosType = photosType;
        this.photosOrder = sharedPreferences.getString(
                a.getString(R.string.key_default_photo_order),
                a.getResources().getStringArray(R.array.photo_order_values)[0]);
        RANDOM_TXT = a.getResources().getStringArray(R.array.photo_order_values)[3];

        this.photosPage = 0;
        this.pageList = new ArrayList<>();

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
        return null;
    }

    @Override
    public void setRequestKey(Object key) {
        // do nothing.
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
        return photosOrder.equals(RANDOM_TXT);
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
        return pageList;
    }

    @Override
    public void setPageList(List<Integer> list) {
        pageList = list;
    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public void setRefreshing(boolean refreshing) {

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
