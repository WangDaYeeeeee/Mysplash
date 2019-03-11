package com.wangdaye.mysplash.common.network.observer;

import com.wangdaye.mysplash.common.basic.model.ListResource;

import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class RandomListResourceObserver<T> extends BaseObserver<List<T>> {

    private MutableLiveData<ListResource<T>> current;
    private List<Integer> pageList;
    private boolean refresh;

    public RandomListResourceObserver(MutableLiveData<ListResource<T>> current,
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
        } else if (current.getValue().getRequestPage() < pageList.size() - 1) {
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
        current.setValue(ListResource.error(current.getValue()));
    }
}
