package com.wangdaye.mysplash.common.network.observer;

import com.wangdaye.mysplash.common.basic.model.Resource;

import androidx.lifecycle.MutableLiveData;

public class ResourceObserver<T> extends BaseObserver<T> {

    private MutableLiveData<Resource<T>> current;

    public ResourceObserver(MutableLiveData<Resource<T>> current) {
        this.current = current;
    }

    @Override
    public void onSucceed(T t) {
        current.setValue(Resource.success(t));
    }

    @Override
    public void onFailed() {
        if (current.getValue() == null) {
            return;
        }
        current.setValue(Resource.error(current.getValue().data));
    }
}
