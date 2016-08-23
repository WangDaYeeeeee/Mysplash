package com.wangdaye.mysplash._common.i.presenter;

import android.content.Context;

/**
 * Photos presenter.
 * */

public interface PhotosPresenter {

    void requestPhotos(Context c, int page, boolean refresh);
    void cancelRequest();

    void refreshNew(Context c, boolean notify);
    void loadMore(Context c, boolean notify);
    void initRefresh(Context c);

    boolean waitingRefresh();
    boolean canLoadMore();

    void setOrder(String key);
}
