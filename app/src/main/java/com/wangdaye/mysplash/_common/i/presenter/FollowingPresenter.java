package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.FollowingAdapter;

/**
 * Following presenter.
 * */

public interface FollowingPresenter {

    void requestFollowingFeed(Context c, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    void setNextPage(String nextPage);
    String getNextPage();

    void setActivityForAdapter(MysplashActivity a);
    int getAdapterItemCount();

    FollowingAdapter getAdapter();
}
