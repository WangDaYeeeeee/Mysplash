package com.wangdaye.main.following;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.i.PagerView;
import com.wangdaye.main.following.ui.adapter.FollowingAdapter;

public class FollowingFeedViewManagePresenter {

    public static <T> void responsePagerListResourceChanged(ListResource<T> resource,
                                                            PagerView view, FollowingAdapter adapter) {
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
            adapter.buildTypeList(0);
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

            ListResource.Event event = resource.consumeEvent();
            if (event instanceof ListResource.DataSetChanged) {
                adapter.buildTypeList(0);
                adapter.notifyDataSetChanged();
            } else if (event instanceof ListResource.ItemRangeInserted) {
                int increase = ((ListResource.ItemRangeInserted) event).increase;
                int positionPhotoStart = adapter.getRealItemCount() - increase;
                int positionTypeStart = adapter.getTypeItemCount();

                adapter.buildTypeList(positionPhotoStart);
                adapter.notifyItemRangeInserted(
                        positionTypeStart,
                        adapter.getTypeItemCount() - positionTypeStart
                );
            } else if (event instanceof ListResource.ItemChanged) {
                int position = adapter.getPhotoHolderAdapterPosition(
                        ((ListResource.ItemChanged) event).index
                );
                adapter.updateItem(
                        position,
                        FollowingAdapter.PAYLOAD_UPDATE_ITEM
                );
            }
        }
    }
}
