package com.wangdaye.mysplash._common.i.presenter;

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

    void setQuery(String key);
    void setOrientation(String key);
}
