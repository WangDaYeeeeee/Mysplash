package com.wangdaye.user.repository;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.unsplash.Collection;
import com.wangdaye.common.network.observer.ListResourceObserver;
import com.wangdaye.common.network.service.CollectionService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class UserCollectionsViewRepository {

    private CollectionService service;

    @Inject
    public UserCollectionsViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getUserCollections(@NonNull MutableLiveData<ListResource<Collection>> current,
                                   String username, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestUserCollections(
                username,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
