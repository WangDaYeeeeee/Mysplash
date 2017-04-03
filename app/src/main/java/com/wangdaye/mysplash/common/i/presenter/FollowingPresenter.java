package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.FollowingAdapter;

/**
 * Following presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.FollowingView}.
 *
 * */

public interface FollowingPresenter {

    void requestFollowingFeed(Context c, boolean refresh);
    void cancelRequest();

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

    void setNextPage(String nextPage);
    String getNextPage();

    void setOver(boolean over);

    void setActivityForAdapter(MysplashActivity a);
    int getAdapterItemCount();

    FollowingAdapter getAdapter();
}
