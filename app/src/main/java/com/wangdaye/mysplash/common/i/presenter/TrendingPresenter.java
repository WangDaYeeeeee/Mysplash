package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Trending presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.TrendingView}.
 *
 * */

public interface TrendingPresenter {

    // HTTP request.

    void requestTrendingFeed(Context c, boolean refresh);
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

    void setNextPage(String nextPage);
    String getNextPage();

    void setOver(boolean over);

    void setActivityForAdapter(MysplashActivity a);
    int getAdapterItemCount();

    PhotoAdapter getAdapter();
}
