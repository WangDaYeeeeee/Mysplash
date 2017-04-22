package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.i.model.MyFollowModel;
import com.wangdaye.mysplash.common.ui.adapter.MyFollowAdapter;

/**
 * My follow implementor.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.MyFollowView}.
 *
 * */

public interface MyFollowPresenter {

    // HTTP request.

    void requestMyFollow(Context c, @Mysplash.PageRule int page, boolean refresh);
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

    // load data interface.

    /** {@link MyFollowModel#getDeltaValue()} */
    int getDeltaValue();
    void setDeltaValue(int delta);

    void setActivityForAdapter(MysplashActivity a);
    MyFollowAdapter getAdapter();
}
