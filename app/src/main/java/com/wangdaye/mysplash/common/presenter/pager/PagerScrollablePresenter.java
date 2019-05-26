package com.wangdaye.mysplash.common.presenter.pager;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Pager auto load presenter.
 * */
public class PagerScrollablePresenter {

    public static void onScrolled(BothWaySwipeRefreshLayout refreshLayout,
                                  RecyclerView recyclerView, int realItemCount,
                                  @Nullable PagerManageView pagerManageView, int index, int dy) {
        if (recyclerView.getLayoutManager() == null) {
            return;
        }
        int[] lastVisibleItems = null;
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPositions(null);
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            lastVisibleItems = new int[] {((GridLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition()};
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            lastVisibleItems = new int[] {((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition()};
        }
        if (lastVisibleItems == null || lastVisibleItems.length == 0) {
            return;
        }

        if (pagerManageView != null && pagerManageView.canLoadMore(index)
                && lastVisibleItems[lastVisibleItems.length - 1] >= realItemCount - Mysplash.DEFAULT_PER_PAGE
                && realItemCount > 0
                && dy > 0) {
            pagerManageView.onLoad(index);
        }
        if (!recyclerView.canScrollVertically(1)
                && pagerManageView != null && pagerManageView.isLoading(index)) {
            refreshLayout.setLoading(true);
        }
    }
}
