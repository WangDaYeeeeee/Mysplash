package com.wangdaye.mysplash.collection.repository;

import com.wangdaye.mysplash.common.basic.model.Resource;
import com.wangdaye.mysplash.common.network.callback.ResourceCallback;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.service.CollectionService;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class CollectionActivityRepository {

    private CollectionService service;

    public CollectionActivityRepository(CollectionService service) {
        this.service = service;
    }

    public void getACollection(@NonNull MutableLiveData<Resource<Collection>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        service.cancel();
        service.requestACollections(id, new ResourceCallback<>(current));
    }

    public void getACuratedCollection(@NonNull MutableLiveData<Resource<Collection>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        service.cancel();
        service.requestACuratedCollections(id, new ResourceCallback<>(current));
    }

    public void cancel() {
        service.cancel();
    }
}
