package com.wangdaye.mysplash.common.utils.presenter;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.model.PagerView;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;

import java.util.List;

public class PagerViewManagePresenter {

    public static <T> void initRefresh(PagerViewModel<T> model, PagerView view) {
        assert model.getListResource().getValue() != null;
        List<T> list = model.getListResource().getValue().dataList;
        list.clear();
        model.setListResource(
                ListResource.refreshSuccess(
                        list, 0, model.getListResource().getValue().perPage, 0));
        view.notifyItemsRefreshed(0);
        model.refresh();
    }

    public static <T> void responsePagerListResourceChanged(ListResource<T> resource, PagerView view) {
        if (resource.dataList.size() == 0
                && (resource.status == ListResource.Status.REFRESHING
                || resource.status == ListResource.Status.LOADING)) {
            // loading state.
            view.setSwipeRefreshing(false);
            view.setSwipeLoading(false);
            view.setPermitSwipeRefreshing(false);
            view.setPermitSwipeLoading(false);
            view.setState(PagerView.State.LOADING);
        } else if (resource.dataList.size() == 0
                && (resource.status == ListResource.Status.REFRESH_ERROR
                || resource.status == ListResource.Status.LOAD_ERROR)) {
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
            view.notifyItemsRefreshed(resource.increase);
            view.setState(PagerView.State.NORMAL);
        } else {
            // normal state control.
            view.setSwipeRefreshing(resource.status == ListResource.Status.REFRESHING);
            if (resource.status == ListResource.Status.REFRESH_SUCCESS) {
                view.notifyItemsRefreshed(resource.increase);
            }
            if (resource.status != ListResource.Status.LOADING) {
                view.setSwipeLoading(false);
            }
            if (resource.status == ListResource.Status.LOAD_SUCCESS
                    || resource.status == ListResource.Status.ALL_LOADED) {
                view.notifyItemsLoaded(resource.increase);
            }
        }

        if (resource.status == ListResource.Status.ALL_LOADED) {
            view.setPermitSwipeLoading(false);
        }
    }
}
