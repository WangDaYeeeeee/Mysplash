package com.wangdaye.mysplash.common.i.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.wangdaye.mysplash.Mysplash;

/**
 * Search presenter.
 *
 * Presenter for {@link com.wangdaye.mysplash.common.i.view.SearchView}.
 *
 * */

public interface SearchPresenter {

    void requestPhotos(Context c, @Mysplash.PageRule int page, boolean refresh);
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

    void setQuery(String key);
    String getQuery();

    void setPage(@Mysplash.PageRule int page);
    void setOver(boolean over);

    int getAdapterItemCount();
    RecyclerView.Adapter getAdapter();
}
