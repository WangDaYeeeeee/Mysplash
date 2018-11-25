package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.SelectedAdapter;

/**
 * Selected presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.SelectedView}.
 *
 * */

public interface SelectedPresenter {

    // HTTP request.

    void requestCollections(Context c, @Mysplash.PageRule int page, boolean refresh);
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

    void setPage(@Mysplash.PageRule int page);
    void setOver(boolean over);

    void setActivityForAdapter(MysplashActivity a);
    SelectedAdapter getAdapter();
}
