package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

/**
 * Collections presenter.
 * */

public interface CollectionsPresenter {

    void requestCollections(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean waitingRefresh();
    boolean canLoadMore();

    void setType(String key);
}
