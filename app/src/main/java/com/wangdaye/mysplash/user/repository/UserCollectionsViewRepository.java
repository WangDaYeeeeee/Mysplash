package com.wangdaye.mysplash.user.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.callback.ListResourceCallback;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.service.CollectionService;

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
                current.getValue().dataPage + 1,
                current.getValue().perPage,
                new ListResourceCallback<>(current, refresh));
    }

    public void cancel() {
        service.cancel();
    }
}
