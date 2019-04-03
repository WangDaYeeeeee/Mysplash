package com.wangdaye.mysplash.common.utils.presenter.pager;

import com.wangdaye.mysplash.common.basic.adapter.FooterAdapter;
import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;

public class PagerViewManagePresenter {

    public static <T> void initRefresh(PagerViewModel<T> model, FooterAdapter adapter) {
        assert model.getListResource().getValue() != null;
        model.setListResource(ListResource.initRefreshing(model.getListResource().getValue()));
        adapter.notifyDataSetChanged();

        model.refresh();
    }

    public static <T> void responsePagerListResourceChanged(ListResource<T> resource,
                                                            PagerView view, FooterAdapter adapter) {
        if (resource.dataList.size() == 0
                && (resource.state == ListResource.State.REFRESHING
                || resource.state == ListResource.State.LOADING)) {
            // loading state.
            view.setSwipeRefreshing(false);
            view.setSwipeLoading(false);
            view.setPermitSwipeRefreshing(false);
            view.setPermitSwipeLoading(false);
            view.setState(PagerView.State.LOADING);
        } else if (resource.dataList.size() == 0
                && resource.state == ListResource.State.ERROR) {
            // error state.
            view.setSwipeRefreshing(false);
            view.setSwipeLoading(false);
            view.setPermitSwipeRefreshing(false);
            view.setPermitSwipeLoading(false);
            view.setState(PagerView.State.ERROR);
        } else if (view.getState() != PagerView.State.NORMAL) {
            // error/loading state -> normal state.
            view.setSwipeRefreshing(false);
            view.setSwipeLoading(false);
            view.setPermitSwipeRefreshing(true);
            view.setPermitSwipeLoading(true);
            adapter.notifyDataSetChanged();
            view.setState(PagerView.State.NORMAL);
        } else {
            // normal state control.
            view.setSwipeRefreshing(resource.state == ListResource.State.REFRESHING);
            if (resource.state != ListResource.State.LOADING) {
                view.setSwipeLoading(false);
            }
            if (resource.state == ListResource.State.ALL_LOADED) {
                view.setPermitSwipeLoading(false);
            }

            if (resource.event instanceof ListResource.DataSetChanged) {
                adapter.notifyDataSetChanged();
            } else if (resource.event instanceof ListResource.ItemRangeInserted) {
                int increase = ((ListResource.ItemRangeInserted) resource.event).increase;
                adapter.notifyItemRangeInserted(adapter.getRealItemCount() - increase, increase);
            } else if (resource.event instanceof ListResource.ItemInserted) {
                adapter.notifyItemInserted(((ListResource.ItemInserted) resource.event).index);
            } else if (resource.event instanceof ListResource.ItemChanged) {
                adapter.notifyItemChanged(
                        ((ListResource.ItemChanged) resource.event).index,
                        FooterAdapter.PAYLOAD_UPDATE_ITEM
                );
            } else if (resource.event instanceof ListResource.ItemRemoved) {
                adapter.notifyItemRemoved(
                        ((ListResource.ItemRemoved) resource.event).index
                );
            }
        }
    }
}
