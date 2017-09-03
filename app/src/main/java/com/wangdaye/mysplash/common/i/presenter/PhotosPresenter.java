package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

import java.util.List;

/**
 * Photos presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.PhotosView}.
 *
 * */

public interface PhotosPresenter {

    // HTTP request.

    void requestPhotos(Context c, @Mysplash.PageRule int page, boolean refresh);
    void cancelRequest();

    // load data interface.

    /**
     * The param notify is used to control the SwipeRefreshLayout. If set true, the
     * SwipeRefreshLayout will show the refresh animation.
     * */
    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    // manage HTTP request parameters.

    Object getRequestKey();
    void setRequestKey(Object k);

    int getPhotosType();
    String getPhotosOrder();

    void setOrder(String key);
    String getOrder();

    void setPage(@Mysplash.PageRule int page);
    void setPageList(List<Integer> pageList);

    void setOver(boolean over);

    void setActivityForAdapter(MysplashActivity a);
    PhotoAdapter getAdapter();
}
