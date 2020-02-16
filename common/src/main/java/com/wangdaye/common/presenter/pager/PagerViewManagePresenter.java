package com.wangdaye.common.presenter.pager;

import com.wangdaye.base.i.PagerView;
import com.wangdaye.base.resource.ListResource;
import com.wangdaye.common.base.adapter.BaseAdapter;
import com.wangdaye.common.base.vm.pager.PagerViewModel;

import java.util.ArrayList;
import java.util.List;

public class PagerViewManagePresenter {

    public static <T> void initRefresh(PagerViewModel<T> model, BaseAdapter<T, ?, ?> adapter) {
        model.writeListResource(ListResource::initRefreshing);
        model.readDataList(adapter::update);
        model.refresh();
    }

    @SuppressWarnings("unchecked")
    public static <T> void responsePagerListResourceChanged(PagerViewModel<T> model, PagerView view,
                                                            BaseAdapter<T, ?, ?> adapter) {
        ListResource.State listState = model.getListState();
        int listSize = model.getListSize();

        if ((listState == ListResource.State.REFRESHING || listState == ListResource.State.LOADING)
                && listSize == 0) {
            // loading state.
            view.setSwipeRefreshing(false);
            view.setSwipeLoading(false);
            view.setPermitSwipeRefreshing(false);
            view.setPermitSwipeLoading(false);
            view.setState(PagerView.State.LOADING);

        } else if (listSize == 0 && listState == ListResource.State.ERROR) {
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

            view.setState(PagerView.State.NORMAL);
            model.readDataList(adapter::update);

        } else {
            // normal state control.
            view.setSwipeRefreshing(listState == ListResource.State.REFRESHING);
            if (listState != ListResource.State.LOADING) {
                view.setSwipeLoading(false);
            }
            if (listState == ListResource.State.ALL_LOADED) {
                view.setPermitSwipeLoading(false);
            }

            ListResource.Event event = model.consumeListEvent();
            if (event instanceof ListResource.DataSetChanged) {
                model.readDataList(adapter::update);
            } else if (event instanceof ListResource.ItemRangeInserted) {
                int increase = ((ListResource.ItemRangeInserted) event).increase;
                model.readDataList(list -> {
                    List<T> appendList = new ArrayList<>(
                            list.subList(listSize - increase, listSize));
                    adapter.addItems(appendList);
                });
            } else if (event instanceof ListResource.ItemInserted) {
                model.readDataList(list -> adapter.addItem(list.get(listSize - 1)));
            } else if (event instanceof ListResource.ItemChanged) {
                int index = ((ListResource.ItemChanged) event).index;
                model.readDataList(list -> adapter.updateItem(list.get(index)));
            } else if (event instanceof ListResource.ItemRemoved) {
                model.readDataList(list -> adapter.removeItem((T) ((ListResource.ItemRemoved) event).item));
            }
        }
    }
}