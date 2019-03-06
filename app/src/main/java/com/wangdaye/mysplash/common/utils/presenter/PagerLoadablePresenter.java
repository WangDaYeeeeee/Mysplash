package com.wangdaye.mysplash.common.utils.presenter;

import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PagerLoadablePresenter {

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FooterAdapter adapter;
    @Nullable private PagerManageView pagerManageView;

    public PagerLoadablePresenter(BothWaySwipeRefreshLayout refreshLayout,
                                  RecyclerView recyclerView, FooterAdapter adapter,
                                  @Nullable PagerManageView pagerManageView) {
        this.refreshLayout = refreshLayout;
        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.pagerManageView = pagerManageView;
    }

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection, int pagerIndex) {
        if ((headDirection && adapter.getRealItemCount() < headIndex)
                || (!headDirection && adapter.getRealItemCount() < headIndex + list.size())) {
            return new ArrayList<>();
        }

        if (!headDirection && pagerManageView != null && pagerManageView.canLoadMore(pagerIndex)) {
            pagerManageView.onLoad(pagerIndex);
        }
        if (!recyclerView.canScrollVertically(1)
                && pagerManageView != null && pagerManageView.isLoading(pagerIndex)) {
            refreshLayout.setLoading(true);
        }

        if (headDirection) {
            if (headIndex == 0) {
                return new ArrayList<>();
            } else {
                return subList(0, headIndex - 1);
            }
        } else {
            if (adapter.getRealItemCount() == headIndex + list.size()) {
                return new ArrayList<>();
            } else {
                return subList(headIndex + list.size(), adapter.getRealItemCount() - 1);
            }
        }
    }

    public abstract List<Photo> subList(int fromIndex, int toIndex);

    public void setPagerManageView(@Nullable PagerManageView v) {
        pagerManageView = v;
    }
}
