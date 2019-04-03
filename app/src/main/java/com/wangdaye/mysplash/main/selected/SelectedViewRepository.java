package com.wangdaye.mysplash.main.selected;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Collection;
import com.wangdaye.mysplash.common.network.observer.ListResourceObserver;
import com.wangdaye.mysplash.common.network.service.CollectionService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class SelectedViewRepository {

    private CollectionService service;

    @Inject
    public SelectedViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getSelected(@NonNull MutableLiveData<ListResource<Collection>> current,
                            boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestUserCollections(
                "unsplash",
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
