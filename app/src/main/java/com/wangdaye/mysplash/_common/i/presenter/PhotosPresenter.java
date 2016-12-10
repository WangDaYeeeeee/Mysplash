package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common.ui._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

/**
 * Photos presenter.
 * */

public interface PhotosPresenter {

    void requestPhotos(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    Object getRequestKey();
    void setRequestKey(Object k);

    int getPhotosType();

    void setOrder(String key);
    String getOrder();

    void setActivityForAdapter(MysplashActivity a);
    PhotoAdapter getAdapter();
}
