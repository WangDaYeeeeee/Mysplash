package com.wangdaye.mysplash.common.basic.model;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Pager view.
 *
 * */

public interface PagerView {

    enum State {
        LOADING, NORMAL, ERROR
    }

    State getState();
    boolean setState(State state);

    void setSelected(boolean selected);
    void setSwipeRefreshing(boolean refreshing);
    void setSwipeLoading(boolean loading);
    void setPermitSwipeRefreshing(boolean permit);
    void setPermitSwipeLoading(boolean permit);

    boolean checkNeedBackToTop();
    void scrollToPageTop();
    boolean canSwipeBack(int dir);

    RecyclerView getRecyclerView();
}
