package com.wangdaye.main.presenter;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.base.vm.pager.PagerViewModel;
import com.wangdaye.main.ui.following.adapter.FollowingAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowingFeedViewManagePresenter {

    public static void initRefresh(PagerViewModel<Photo> model, FollowingAdapter adapter) {
        model.writeListResource(ListResource::initRefreshing);
        model.readDataList(adapter::update);
        model.refresh();
    }

    public static void responsePagerListResourceChanged(PagerViewModel<Photo> model,
                                                        PagerView view, FollowingAdapter adapter) {
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
                    List<Photo> appendList = new ArrayList<>(
                            list.subList(listSize - increase, listSize));
                    adapter.addItems(appendList);
                });
            } else if (event instanceof ListResource.ItemChanged) {
                int index = ((ListResource.ItemChanged) event).index;
                model.readDataList(list -> adapter.updateItem(list.get(index)));
            }
        }
    }
}
