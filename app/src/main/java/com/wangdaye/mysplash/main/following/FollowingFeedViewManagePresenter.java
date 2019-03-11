package com.wangdaye.mysplash.main.following;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.main.following.ui.FollowingAdapter;

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

            if (resource.event instanceof ListResource.DataSetChanged) {
                adapter.buildTypeList(0);
                adapter.notifyDataSetChanged();
            } else if (resource.event instanceof ListResource.ItemRangeInserted) {
                int increase = ((ListResource.ItemRangeInserted) resource.event).increase;
                int positionPhotoStart = adapter.getRealItemCount() - increase;
                int positionTypeStart = adapter.getTypeItemCount();
                adapter.buildTypeList(positionPhotoStart);
                adapter.notifyItemRangeInserted(
                        positionTypeStart, adapter.getTypeItemCount() - positionTypeStart);
            } else if (resource.event instanceof ListResource.ItemChanged) {
                int position = adapter.getPhotoHolderAdapterPosition(
                        ((ListResource.ItemChanged) resource.event).index);
                adapter.notifyItemChanged(position, FollowingAdapter.PAYLOAD_UPDATE_ITEM);
            }
        }
    }
}
