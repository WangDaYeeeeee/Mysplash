package com.wangdaye.mysplash.common.network.callback;

import com.wangdaye.mysplash.common.basic.model.ListResource;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class RandomListResourceCallback<T> extends Callback<List<T>> {

    private MutableLiveData<ListResource<T>> current;
    private List<Integer> pageList;
    private boolean refresh;

    public RandomListResourceCallback(MutableLiveData<ListResource<T>> current,
                                      List<Integer> pageList, boolean refresh) {
        this.current = current;
        this.pageList = pageList;
        this.refresh = refresh;
    }

    @Override
    public void onSucceed(List<T> list) {
        if (current.getValue() == null) {
            return;
        }
        if (refresh) {
            current.setValue(ListResource.refreshSuccess(current.getValue(), list));
        } else if (current.getValue().dataPage + 1 < pageList.size() - 1) {
            current.setValue(ListResource.loadSuccess(current.getValue(), list));
        } else {
            current.setValue(ListResource.allLoaded(current.getValue(), list));
        }
    }

    @Override
    public void onFailed() {
        if (current.getValue() == null) {
            return;
        }
        if (refresh) {
            current.setValue(ListResource.refreshError(current.getValue()));
        } else {
            current.setValue(ListResource.loadError(current.getValue()));
        }
    }
}
