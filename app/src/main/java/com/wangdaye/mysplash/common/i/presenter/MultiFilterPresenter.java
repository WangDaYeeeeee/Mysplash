package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.common.basic.activity.MysplashActivity;
import com.wangdaye.mysplash.common.ui.adapter.PhotoAdapter;

/**
 * Multi-filter presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.MultiFilterView}.
 *
 * */

public interface MultiFilterPresenter {

    // HTTP request.

    void requestPhotos(Context c, boolean refresh);
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

    void setQuery(String query);
    String getQuery();

    void setUsername(String username);
    String getUsername();

    void setOrientation(String o);
    String getOrientation();

    void setFeatured(boolean f);
    boolean isFeatured();

    void setOver(boolean over);

    int getAdapterItemCount();
    void setActivityForAdapter(MysplashActivity a);
    PhotoAdapter getAdapter();
}
