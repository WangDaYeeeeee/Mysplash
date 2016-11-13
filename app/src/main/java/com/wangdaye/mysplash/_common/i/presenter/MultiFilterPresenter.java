package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common.ui.activity.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;

/**
 * Multi-filter presenter.
 * */

public interface MultiFilterPresenter {

    void requestPhotos(Context c, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    void setQuery(String query);
    String getQuery();

    void setUsername(String username);
    String getUsername();

    void setCategory(int c);
    int getCategory();

    void setOrientation(String o);
    String getOrientation();

    void setFeatured(boolean f);
    boolean isFeatured();

    int getAdapterItemCount();
    void setActivityForAdapter(MysplashActivity a);
    PhotoAdapter getAdapter();
}
