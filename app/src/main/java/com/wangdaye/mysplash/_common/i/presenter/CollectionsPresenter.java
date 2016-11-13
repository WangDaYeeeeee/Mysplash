package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.CollectionAdapter;

/**
 * Collections presenter.
 * */

public interface CollectionsPresenter {

    void requestCollections(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    Object getRequestKey();
    void setRequestKey(Object k);

    void setType(String key);
    String getType();
    void setActivityForAdapter(MysplashActivity a);
    CollectionAdapter getAdapter();
}
