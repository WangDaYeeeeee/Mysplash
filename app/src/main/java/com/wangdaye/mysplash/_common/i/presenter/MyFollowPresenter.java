package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.MyFollowAdapter;

/**
 * My follow implementor.
 * */

public interface MyFollowPresenter {

    void requestMyFollow(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    int getDeltaValue();
    void setDeltaValue(int delta);

    void setActivityForAdapter(MysplashActivity a);
    MyFollowAdapter getAdapter();
}
