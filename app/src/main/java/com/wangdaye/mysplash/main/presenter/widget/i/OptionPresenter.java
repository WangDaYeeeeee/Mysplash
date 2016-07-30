package com.wangdaye.mysplash.main.presenter.widget.i;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Option feedback presenter.
 * */

public interface OptionPresenter {

    boolean checkNeedRefresh();
    boolean checkNeedChangOrder(String order);

    void refreshNew(Context c);
    void loadMore(Context c);
    void initRefresh(Context c);
    void doSearch(Context c, String query, String orientation);
    void autoLoad(RecyclerView recyclerView, int dy);

    void cancelRequest();
}
