package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

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
    String getQuery();

    int getAdapterItemCount();
    RecyclerView.Adapter getAdapter();
}
