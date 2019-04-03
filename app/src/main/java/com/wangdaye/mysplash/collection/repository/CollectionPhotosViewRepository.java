package com.wangdaye.mysplash.collection.repository;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.network.observer.ListResourceObserver;
import com.wangdaye.mysplash.common.network.service.PhotoService;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class CollectionPhotosViewRepository {

    private PhotoService service;

    @Inject
    public CollectionPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getCollectionPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                                    int collectionId, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestCollectionPhotos(
                collectionId,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void getCuratedCollectionPhotos(@NonNull MutableLiveData<ListResource<Photo>> current,
                                           int collectionId, boolean refresh) {
        assert current.getValue() != null;
        if (refresh) {
            current.setValue(ListResource.refreshing(current.getValue()));
        } else {
            current.setValue(ListResource.loading(current.getValue()));
        }

        service.cancel();
        service.requestCuratedCollectionPhotos(
                collectionId,
                current.getValue().getRequestPage(),
                current.getValue().perPage,
                new ListResourceObserver<>(current, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
