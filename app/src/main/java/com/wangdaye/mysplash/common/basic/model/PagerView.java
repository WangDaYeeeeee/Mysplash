package com.wangdaye.mysplash.common.basic.model;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Pager view.
 *
 * A view which as an abstract concept for "pager".
 *
 * */

public interface PagerView {

    enum State {
        LOADING, NORMAL, ERROR
    }

    State getState();
    boolean setState(State state);

    void notifyItemsRefreshed(int count);
    void notifyItemsLoaded(int count);

    void setSelected(boolean selected);
    void setSwipeRefreshing(boolean refreshing);
    void setSwipeLoading(boolean loading);
    void setPermitSwipeRefreshing(boolean permit);
    void setPermitSwipeLoading(boolean permit);

    boolean checkNeedBackToTop();
    void scrollToPageTop();
    boolean canSwipeBack(int dir);
    int getItemCount();

    RecyclerView getRecyclerView();
    RecyclerView.Adapter getRecyclerViewAdapter();
}
