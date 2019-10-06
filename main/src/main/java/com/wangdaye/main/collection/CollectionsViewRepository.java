package com.wangdaye.main.collection;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.CollectionService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class CollectionsViewRepository {

    private CollectionService service;

    @Inject
    public CollectionsViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getAllCollections(@NonNull MutableLiveData<ListResource<Collection>> current,
                                  boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestAllCollections(
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void getCuratedCollections(@NonNull MutableLiveData<ListResource<Collection>> current,
                                      boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestCuratedCollections(
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void getFeaturedCollections(@NonNull MutableLiveData<ListResource<Collection>> current,
                                       boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestFeaturedCollections(
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
