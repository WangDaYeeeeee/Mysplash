package com.wangdaye.mysplash.user.model.widget;

import android.support.annotation.IntDef;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.entity.unsplash.User;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotosModel;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

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

    @TypeRule
    private int photosType;
    private String photosOrder;

    private int photosPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public static final int PHOTOS_TYPE_PHOTOS = 0;
    public static final int PHOTOS_TYPE_LIKES = 1;
    @IntDef({PHOTOS_TYPE_PHOTOS, PHOTOS_TYPE_LIKES})
    private @interface TypeRule {}

    /** <br> life cycle. */

    public PhotosObject(UserActivity a, User u, @TypeRule int photosType) {
        this.adapter = new PhotoAdapter(a, new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE), a, a);
        this.service = PhotoService.getService();

        this.requestKey = u;

        this.photosType = photosType;
        this.photosOrder = SettingsOptionManager.getInstance(a).getDefaultPhotoOrder();

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
