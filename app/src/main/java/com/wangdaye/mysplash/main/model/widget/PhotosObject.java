package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.i.model.PhotosModel;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Photos object.
 *
 * A {@link PhotosModel} for {@link com.wangdaye.mysplash.main.view.widget.HomePhotosView}.
 *
 * */

public class PhotosObject
        implements PhotosModel {

    private PhotoAdapter adapter;
    private PhotoService service;

    @Mysplash.PhotosTypeRule
    private int photosType;
    private String photosOrder;

    private int photosPage;
    private List<Integer> pageList;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    public static final int PHOTOS_TYPE_NEW = 0;
    public static final int PHOTOS_TYPE_FEATURED = 1;
    private final String RANDOM_TXT;

    public PhotosObject(MysplashActivity a,
                        PhotoAdapter adapter, @Mysplash.PhotosTypeRule int photosType) {
        this.adapter = adapter;
        this.service = PhotoService.getService();

        this.photosType = photosType;
        this.photosOrder = SettingsOptionManager.getInstance(a).getDefaultPhotoOrder();
        RANDOM_TXT = a.getResources().getStringArray(R.array.photo_order_values)[3];

        this.photosPage = adapter.getRealItemCount() / Mysplash.DEFAULT_PER_PAGE;
        this.pageList = new ArrayList<>();

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
        pageList.addAll(list);
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
