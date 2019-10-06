package com.wangdaye.collection.repository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wangdaye.base.resource.Resource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.network.observer.ResourceObserver;
import com.wangdaye.common.network.service.CollectionService;

public class CollectionActivityRepository {

    private CollectionService service;

    @Inject
    public CollectionActivityRepository(CollectionService service) {
        this.service = service;
    }

    public void getACollection(@NonNull MutableLiveData<Resource<Collection>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        service.cancel();
        service.requestACollections(id, new ResourceObserver<>(current));
    }

    public void getACuratedCollection(@NonNull MutableLiveData<Resource<Collection>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        service.cancel();
        service.requestACuratedCollections(id, new ResourceObserver<>(current));
    }

    public void cancel() {
        service.cancel();
    }
}
