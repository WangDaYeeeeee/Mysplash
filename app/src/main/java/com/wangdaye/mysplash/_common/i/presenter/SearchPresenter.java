package com.wangdaye.mysplash._common.i.presenter;

import android.app.Activity;
import android.content.Context;

/**
 * Search presenter.
 * */

public interface SearchPresenter {

    void requestPhotos(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean canLoadMore();
    boolean isRefreshing();
    boolean isLoading();

    void setQuery(String key);
    void setOrientation(String key);
    void setActivityForAdapter(Activity a);
    int getAdapterItemCount();
}
