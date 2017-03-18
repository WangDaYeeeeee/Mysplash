package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

import java.util.List;

/**
 * Category presenter.
 * */

public interface CategoryPresenter {

    void requestPhotos(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    void setCategory(int key);
    void setOrder(String key);
    String getOrder();

    void setPage(int page);
    void setPageList(List<Integer> pageList);

    void setOver(boolean over);

    void setActivityForAdapter(MysplashActivity a);
    int getAdapterItemCount();

    PhotoAdapter getAdapter();
}
