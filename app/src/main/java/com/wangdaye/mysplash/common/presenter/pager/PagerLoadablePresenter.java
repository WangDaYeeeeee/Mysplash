package com.wangdaye.mysplash.common.presenter.pager;

import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.basic.model.PagerManageView;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.network.json.Photo;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

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
