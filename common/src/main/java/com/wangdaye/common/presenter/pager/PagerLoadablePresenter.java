package com.wangdaye.common.presenter.pager;

import androidx.recyclerview.widget.RecyclerView;

import com.wangdaye.common.base.adapter.footerAdapter.FooterAdapter;
import com.wangdaye.base.i.PagerManageView;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.base.unsplash.Photo;

import java.util.ArrayList;
import java.util.List;

public abstract class PagerLoadablePresenter {

    public List<Photo> loadMore(List<Photo> list, int headIndex, boolean headDirection,
                                PagerView pagerView, RecyclerView recyclerView, FooterAdapter adapter,
                                PagerManageView pagerManageView, int pagerIndex) {
        if ((headDirection && adapter.getRealItemCount() < headIndex)
                || (!headDirection && adapter.getRealItemCount() < headIndex + list.size())) {
            return new ArrayList<>();
        }

        if (!headDirection && pagerManageView != null && pagerManageView.canLoadMore(pagerIndex)) {
            pagerManageView.onLoad(pagerIndex);
        }
        if (!recyclerView.canScrollVertically(1)
                && pagerManageView != null && pagerManageView.isLoading(pagerIndex)) {
            pagerView.setSwipeLoading(true);
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
}
