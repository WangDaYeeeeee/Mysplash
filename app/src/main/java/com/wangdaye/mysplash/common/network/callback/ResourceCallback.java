package com.wangdaye.mysplash.common.network.callback;

import com.wangdaye.mysplash.common.basic.model.Resource;

import androidx.lifecycle.MutableLiveData;

public class ResourceCallback<T> extends Callback<T> {

    private MutableLiveData<Resource<T>> current;

    public ResourceCallback(MutableLiveData<Resource<T>> current) {
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
