package com.wangdaye.common.base.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wangdaye.base.resource.Resource;

/**
 * Browsable view model.
 * */
public abstract class BrowsableViewModel<T> extends ViewModel {

    private MutableLiveData<Resource<T>> resource;

    public BrowsableViewModel() {
        resource = null;
    }

    protected boolean init(@NonNull Resource<T> r) {
        if (resource == null) {
            resource = new MutableLiveData<>();
            resource.setValue(r);
            return true;
        }
        return false;
    }

    public MutableLiveData<Resource<T>> getResource() {
        return resource;
    }

    public void setResource(@NonNull Resource<T> r) {
        resource.setValue(r);
    }
}
